import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.stream.Stream;

public final class KiemThu {
    private static int dat;
    private static int hong;
    private static int soKiemTra;
    private static final LocalDate NGAY = LocalDate.of(2030, 1, 2);
    private static final Clock DONG_HO = Clock.fixed(Instant.parse("2030-01-02T03:00:00Z"),
            ZoneId.of("Asia/Ho_Chi_Minh"));

    private KiemThu() { }

    private static CuaHang moi() {
        CuaHang ch = new CuaHang(DONG_HO);
        ch.getSanPham().them(new DoUong("DU01", "Cà phê sữa", 25000, 10, 0, LocalDate.MAX));
        ch.getSanPham().them(new ThucPham("TP01", "Bánh mì", 20000, 10, 10, LocalDate.MAX));
        ch.themKhachHang(new KhachHang("KH01", "Khách Một", "0900000001"));
        ch.themNhanVien(new NhanVien("NV01", "Nhân Viên Một", "0900000002"));
        return ch;
    }

    private static Map<String, Integer> gio(int doUong, int thucPham) {
        Map<String, Integer> gio = new LinkedHashMap<>();
        if (doUong != 0) gio.put("DU01", doUong);
        if (thucPham != 0) gio.put("TP01", thucPham);
        return gio;
    }

    private static void dung(boolean dieuKien, String moTa) {
        soKiemTra++;
        if (!dieuKien) throw new AssertionError(moTa);
    }

    private static void loi(Class<? extends Throwable> loai, Runnable hanhDong) {
        Throwable batDuoc = null;
        try { hanhDong.run(); } catch (Throwable e) { batDuoc = e; }
        dung(batDuoc != null && loai.isInstance(batDuoc),
                "Cần lỗi " + loai.getSimpleName() + ", thực tế: " + batDuoc);
    }

    private static void ca(String ten, Runnable hanhDong) {
        try {
            hanhDong.run();
            dat++;
            System.out.println("[PASS] " + ten);
        } catch (Throwable e) {
            hong++;
            System.out.println("[FAIL] " + ten + " -> " + e);
        }
    }

    public static void main(String[] args) {
        ca("01 Chuẩn hóa mã, tên và tìm kiếm tiếng Việt", () -> {
            dung(QuyTac.chuanHoaMa(" du01 ").equals("DU01"), "Mã in hoa");
            dung(QuyTac.chuanHoaChuoi("  Cà   phê  ").equals("Cà phê"), "Gộp khoảng trắng");
            dung(QuyTac.khoaTimKiem("ĐỒ UỐNG").equals("do uong"), "Bỏ dấu");
        });
        ca("02 Chặn mã rỗng, mã sai và tên rỗng", () -> {
            for (String ma : List.of("", "A B", "-A", "Mã", "A".repeat(21))) {
                loi(IllegalArgumentException.class, () -> QuyTac.chuanHoaMa(ma));
            }
            loi(IllegalArgumentException.class, () -> QuyTac.chuanHoaChuoi("  "));
        });
        ca("03 Biên giá gốc, giảm giá, tồn kho", () -> {
            loi(IllegalArgumentException.class, () -> new DoUong("A", "A", 0, 1, 0, NGAY));
            loi(IllegalArgumentException.class, () -> new DoUong("A", "A", QuyTac.GIA_TOI_DA + 1, 1, 0, NGAY));
            loi(IllegalArgumentException.class, () -> new DoUong("A", "A", 1, -1, 0, NGAY));
            loi(IllegalArgumentException.class, () -> new DoUong("A", "A", 1, 1, 101, NGAY));
            loi(IllegalArgumentException.class, () -> new DoUong("A", "A", 1, 1, -1, NGAY));
        });
        ca("04 Giá khuyến mãi và làm tròn đến đồng", () -> {
            dung(new DoUong("A", "A", 25000, 1, 10, NGAY).giaBanThucTe() == 22500, "Giảm 10%");
            dung(new DoUong("A", "A", 101, 1, 50, NGAY).giaBanThucTe() == 51, "50,5 làm tròn 51");
            dung(new DoUong("A", "A", 25000, 1, 100, NGAY).giaBanThucTe() == 0, "Giảm 100%");
        });
        ca("05 Đa hình SanPham, interface KhuyenMai và Nguoi", () -> {
            SanPham sp = new ThucPham("A", "A", 20000, 1, 10, NGAY);
            KhuyenMai km = sp;
            dung(sp.getLoai().equals("THUC_PHAM") && km.giaBanThucTe() == 18000, "Đa hình sản phẩm");
            Nguoi nguoi = new NhanVien("NV1", "An", "0900000001");
            dung(nguoi.getVaiTro().equals("Nhân viên"), "Đa hình người");
        });
        ca("06 Cả hai loại được bán trong ngày HSD, cấm bán ngày sau", () -> {
            for (SanPham sp : List.of(new DoUong("A", "A", 1, 1, 0, NGAY),
                    new ThucPham("B", "B", 1, 1, 0, NGAY))) {
                dung(sp.duocBan(NGAY.minusDays(1)), "Ngày trước HSD");
                dung(sp.duocBan(NGAY), "Ngày đúng HSD");
                dung(!sp.duocBan(NGAY.plusDays(1)), "Ngày sau HSD");
            }
        });
        ca("07 Mã sản phẩm trùng sau chuẩn hóa", () -> {
            CuaHang ch = moi();
            loi(IllegalArgumentException.class, () -> ch.getSanPham().them(new DoUong(" du01 ", "Khác", 1, 1, 0, NGAY)));
            dung(ch.getSanPham().tatCa().size() == 2, "Không thêm trùng");
        });
        ca("08 Tìm theo mã/tên không phân biệt hoa thường và dấu", () -> {
            DanhSachSanPham ds = moi().getSanPham();
            dung(ds.timTheoMa(" du01 ") != null, "Mã đã chuẩn hóa");
            dung(ds.timTheoTen("CA PHE").size() == 1, "Tên không dấu");
            dung(ds.timTheoMa("KHONGCO") == null, "Mã không có");
            dung(ds.timTheoTen("KHONGCO").isEmpty(), "Tên không có");
        });
        ca("09 Lọc loại, hết hàng và hết hạn độc lập", () -> {
            DanhSachSanPham ds = moi().getSanPham();
            ds.them(new DoUong("HH", "Hết hàng", 1, 0, 0, NGAY.plusDays(1)));
            ds.them(new ThucPham("HE", "Hết hạn", 1, 3, 0, NGAY.minusDays(1)));
            dung(ds.locTheoLoai("DO_UONG").size() == 2, "Lọc loại");
            dung(ds.locTheoTrangThai(1, NGAY).size() == 2, "Còn bán");
            dung(ds.locTheoTrangThai(2, NGAY).size() == 1, "Hết hạn");
            dung(ds.locTheoTrangThai(3, NGAY).size() == 1, "Hết hàng");
        });
        ca("10 Sửa sản phẩm giữ mã, loại và tồn kho", () -> {
            CuaHang ch = moi();
            ch.getSanPham().sua(new DoUong("DU01", "Tên mới", 30000, 10, 5, NGAY));
            dung(ch.batBuocCoSanPham("DU01").getTen().equals("Tên mới"), "Sửa thành công");
            loi(IllegalArgumentException.class, () -> ch.getSanPham().sua(new ThucPham("DU01", "A", 1, 10, 0, NGAY)));
            loi(IllegalArgumentException.class, () -> ch.getSanPham().sua(new DoUong("DU01", "A", 1, 99, 0, NGAY)));
            dung(ch.batBuocCoSanPham("DU01").getTonKho() == 10, "Không sửa lén tồn kho");
        });
        ca("11 Xóa sản phẩm chưa có hóa đơn và chặn mã không tồn tại", () -> {
            CuaHang ch = moi();
            ch.xoaSanPham("DU01");
            dung(ch.getSanPham().tatCa().size() == 1, "Xóa thành công");
            loi(IllegalArgumentException.class, () -> ch.xoaSanPham("DU01"));
        });
        ca("12 Regex điện thoại đúng 10 chữ số và bắt đầu 0", () -> {
            dung(QuyTac.kiemTraSoDienThoai("0900000001").equals("0900000001"), "SĐT hợp lệ");
            for (String s : List.of("900000001", "1900000001", "09000000011", "090-000001", "09abcdefgh")) {
                loi(IllegalArgumentException.class, () -> QuyTac.kiemTraSoDienThoai(s));
            }
        });
        ca("13 Thêm trùng nhân sự và sửa sai SĐT không đổi dở tên", () -> {
            CuaHang ch = moi();
            loi(IllegalArgumentException.class, () -> ch.themNhanVien(new NhanVien("nv01", "A", "0900000001")));
            loi(IllegalArgumentException.class, () -> ch.themKhachHang(new KhachHang("kh01", "A", "0900000001")));
            loi(IllegalArgumentException.class, () -> ch.suaNhanVien("NV01", "Tên mới", "123"));
            dung(ch.timNhanVien("NV01").getTen().equals("Nhân Viên Một"), "Tên không thay đổi");
            ch.suaKhachHang("KH01", "Khách Mới", "0900000009");
            dung(ch.timKhachHang("KH01").getTen().equals("Khách Mới"), "Sửa khách hợp lệ");
        });
        ca("14 Nhập kho hợp lệ; chặn âm, 0 và vượt giới hạn", () -> {
            CuaHang ch = moi();
            ch.nhapKho("DU01", 5);
            dung(ch.batBuocCoSanPham("DU01").getTonKho() == 15, "Tăng kho");
            loi(IllegalArgumentException.class, () -> ch.nhapKho("DU01", 0));
            loi(IllegalArgumentException.class, () -> ch.nhapKho("DU01", -1));
            loi(IllegalArgumentException.class, () -> ch.nhapKho("DU01", Integer.MAX_VALUE));
            dung(ch.batBuocCoSanPham("DU01").getTonKho() == 15, "Kho không đổi sau lỗi");
        });
        ca("15 Giỏ trống, số lượng âm, 0, null và mã không có", () -> {
            CuaHang ch = moi();
            loi(IllegalArgumentException.class, () -> ch.banHang("", "NV01", Map.of(), 0));
            loi(IllegalArgumentException.class, () -> ch.kiemTraGioHang(Map.of("DU01", -1)));
            loi(IllegalArgumentException.class, () -> ch.kiemTraGioHang(Map.of("DU01", 0)));
            Map<String, Integer> sai = new LinkedHashMap<>(); sai.put("DU01", null);
            loi(IllegalArgumentException.class, () -> ch.kiemTraGioHang(sai));
            loi(IllegalArgumentException.class, () -> ch.kiemTraGioHang(Map.of("XXX", 1)));
        });
        ca("16 Thiếu kho ở dòng sau không trừ kho dòng trước", () -> {
            CuaHang ch = moi();
            loi(IllegalArgumentException.class, () -> ch.banHang("", "NV01", gio(1, 11), 999999));
            dung(ch.batBuocCoSanPham("DU01").getTonKho() == 10, "Không trừ dở kho");
            dung(ch.getHoaDon().isEmpty() && ch.tongDoanhThu() == 0, "Không ghi hóa đơn/doanh thu");
        });
        ca("17 Mặt hàng hết hạn khiến toàn bộ giao dịch bị từ chối", () -> {
            CuaHang ch = moi();
            ch.getSanPham().sua(new ThucPham("TP01", "Hết hạn", 20000, 10, 10, NGAY.minusDays(1)));
            loi(IllegalArgumentException.class, () -> ch.banHang("", "NV01", gio(1, 1), 100000));
            dung(ch.batBuocCoSanPham("DU01").getTonKho() == 10, "Kho không đổi");
        });
        ca("18 Khách/nhân viên không tồn tại không phát sinh hóa đơn", () -> {
            CuaHang ch = moi();
            loi(IllegalArgumentException.class, () -> ch.banHang("KH99", "NV01", gio(1, 0), 50000));
            loi(IllegalArgumentException.class, () -> ch.banHang("", "NV99", gio(1, 0), 50000));
            dung(ch.getHoaDon().isEmpty(), "Không có hóa đơn");
        });
        ca("19 Thiếu tiền không trừ kho hoặc tiêu thụ mã hóa đơn", () -> {
            CuaHang ch = moi();
            loi(IllegalArgumentException.class, () -> ch.banHang("", "NV01", gio(2, 1), 67999));
            dung(ch.batBuocCoSanPham("DU01").getTonKho() == 10, "Kho không đổi");
            dung(ch.banHang("", "NV01", gio(1, 0), 25000).getMa().equals("HD000001"), "Không nhảy mã");
        });
        ca("20 Thanh toán nhiều món, giá giảm, tiền thừa và tồn kho", () -> {
            CuaHang ch = moi();
            HoaDon hd = ch.banHang("KH01", "NV01", gio(2, 1), 100000);
            dung(hd.tongTien() == 68000 && hd.tienThua() == 32000, "68.000 và 32.000");
            dung(ch.batBuocCoSanPham("DU01").getTonKho() == 8, "Trừ đồ uống");
            dung(ch.batBuocCoSanPham("TP01").getTonKho() == 9, "Trừ thực phẩm");
            dung(ch.tongDoanhThu() == 68000 && !hd.isDaHuy(), "Doanh thu tăng đúng");
            dung(hd.getNgay().toLocalDate().equals(NGAY), "Ngày lấy từ đồng hồ");
        });
        ca("21 Chặn hai khóa giỏ trùng mã sau chuẩn hóa", () -> {
            CuaHang ch = moi();
            Map<String, Integer> g = new LinkedHashMap<>(); g.put("DU01", 6); g.put("du01", 6);
            loi(IllegalArgumentException.class, () -> ch.banHang("", "NV01", g, 999999));
            dung(ch.batBuocCoSanPham("DU01").getTonKho() == 10, "Không vượt kho qua mã khác hoa/thường");
        });
        ca("22 Khuyến mãi 100% vẫn tạo hóa đơn và trừ kho", () -> {
            CuaHang ch = moi();
            ch.getSanPham().sua(new DoUong("DU01", "Miễn phí", 25000, 10, 100, NGAY));
            HoaDon hd = ch.banHang("", "NV01", gio(1, 0), 0);
            dung(hd.tongTien() == 0 && hd.tienThua() == 0, "Tiền 0");
            dung(ch.batBuocCoSanPham("DU01").getTonKho() == 9, "Vẫn trừ kho");
            dung(hd.getTenKhachHang().equals("Khách vãng lai"), "Khách không có mã");
        });
        ca("23 Snapshot không đổi khi sửa tên/giá/khuyến mãi sản phẩm", () -> {
            CuaHang ch = moi();
            HoaDon hd = ch.banHang("KH01", "NV01", gio(1, 0), 50000);
            ch.getSanPham().sua(new DoUong("DU01", "Tên mới", 90000, 9, 50, NGAY));
            dung(hd.tongTien() == 25000, "Tổng tiền cũ");
            dung(hd.getChiTiet().get(0).getTenSanPham().equals("Cà phê sữa"), "Tên cũ");
            dung(ch.banHang("", "NV01", gio(1, 0), 50000).tongTien() == 45000, "Hóa đơn mới dùng giá mới");
        });
        ca("24 Snapshot tên khách và nhân viên được giữ nguyên", () -> {
            CuaHang ch = moi();
            HoaDon hd = ch.banHang("KH01", "NV01", gio(1, 0), 25000);
            ch.suaKhachHang("KH01", "Khách Mới", "0900000003");
            ch.suaNhanVien("NV01", "Nhân Viên Mới", "0900000004");
            dung(hd.getTenKhachHang().equals("Khách Một"), "Tên khách cũ");
            dung(hd.getTenNhanVien().equals("Nhân Viên Một"), "Tên nhân viên cũ");
        });
        ca("25 Sửa giỏ sau thanh toán không thay đổi hóa đơn", () -> {
            CuaHang ch = moi(); Map<String, Integer> g = gio(1, 0);
            HoaDon hd = ch.banHang("", "NV01", g, 25000);
            g.put("DU01", 9); g.clear();
            dung(hd.getChiTiet().get(0).getSoLuong() == 1, "Số lượng snapshot");
        });
        ca("26 Hủy hoàn đúng kho, trả đúng tiền và loại doanh thu", () -> {
            CuaHang ch = moi(); HoaDon hd = ch.banHang("", "NV01", gio(2, 1), 100000);
            dung(ch.huyHoaDon(hd.getMa()) == 68000, "Hoàn tiền hàng, không hoàn 100.000");
            dung(ch.batBuocCoSanPham("DU01").getTonKho() == 10, "Hoàn đồ uống");
            dung(ch.batBuocCoSanPham("TP01").getTonKho() == 10, "Hoàn thực phẩm");
            dung(ch.tongDoanhThu() == 0 && hd.isDaHuy(), "Doanh thu và trạng thái");
            dung(hd.tongTien() == 68000, "Giữ giá trị hóa đơn lịch sử");
        });
        ca("27 Hủy hai lần hoặc mã không có không hoàn kho thêm", () -> {
            CuaHang ch = moi(); HoaDon hd = ch.banHang("", "NV01", gio(1, 0), 25000);
            ch.huyHoaDon(hd.getMa());
            loi(IllegalArgumentException.class, () -> ch.huyHoaDon(hd.getMa()));
            loi(IllegalArgumentException.class, () -> ch.huyHoaDon("HD999999"));
            dung(ch.batBuocCoSanPham("DU01").getTonKho() == 10, "Chỉ hoàn một lần");
        });
        ca("28 Hoàn kho vượt trần ở dòng sau không sửa dòng trước", () -> {
            CuaHang ch = moi(); HoaDon hd = ch.banHang("", "NV01", gio(1, 1), 50000);
            ch.nhapKho("TP01", QuyTac.TON_KHO_TOI_DA - 9);
            loi(IllegalArgumentException.class, () -> ch.huyHoaDon(hd.getMa()));
            dung(ch.batBuocCoSanPham("DU01").getTonKho() == 9, "Không hoàn dở dòng đầu");
            dung(!hd.isDaHuy() && ch.tongDoanhThu() == 43000, "Hóa đơn/doanh thu không đổi");
        });
        ca("29 Chặn xóa sản phẩm/người đã được hóa đơn tham chiếu", () -> {
            CuaHang ch = moi(); HoaDon hd = ch.banHang("KH01", "NV01", gio(1, 0), 25000);
            loi(IllegalArgumentException.class, () -> ch.xoaSanPham("DU01"));
            loi(IllegalArgumentException.class, () -> ch.xoaKhachHang("KH01"));
            loi(IllegalArgumentException.class, () -> ch.xoaNhanVien("NV01"));
            ch.huyHoaDon(hd.getMa());
            loi(IllegalArgumentException.class, () -> ch.xoaSanPham("DU01"));
        });
        ca("30 Xóa người chưa có hóa đơn", () -> {
            CuaHang ch = moi(); ch.xoaKhachHang("KH01"); ch.xoaNhanVien("NV01");
            dung(ch.getKhachHang().isEmpty() && ch.getNhanVien().isEmpty(), "Xóa thành công");
            loi(IllegalArgumentException.class, () -> ch.xoaKhachHang("KH01"));
        });
        ca("31 Doanh thu bao gồm biên ngày và loại hóa đơn hủy", () -> {
            CuaHang ch = moi(); ch.banHang("", "NV01", gio(1, 0), 25000);
            HoaDon huy = ch.banHang("", "NV01", gio(0, 1), 18000); ch.huyHoaDon(huy.getMa());
            dung(ch.doanhThu(NGAY, NGAY) == 25000, "Đúng ngày");
            dung(ch.doanhThu(NGAY.minusDays(1), NGAY) == 25000, "Biên cuối");
            dung(ch.doanhThu(NGAY, NGAY.plusDays(1)) == 25000, "Biên đầu");
            dung(ch.doanhThu(NGAY.plusDays(1), NGAY.plusDays(1)) == 0, "Ngoài kỳ");
            loi(IllegalArgumentException.class, () -> ch.doanhThu(NGAY.plusDays(1), NGAY));
        });
        ca("32 Thống kê số lượng bán ròng", () -> {
            CuaHang ch = moi(); ch.banHang("", "NV01", gio(2, 1), 100000);
            HoaDon hd = ch.banHang("", "NV01", gio(1, 0), 25000); ch.huyHoaDon(hd.getMa());
            dung(ch.soLuongDaBan().get("DU01") == 2 && ch.soLuongDaBan().get("TP01") == 1, "Loại số lượng đã hủy");
        });
        ca("33 Không dùng lại mã hóa đơn đã hủy", () -> {
            CuaHang ch = moi(); HoaDon hd = ch.banHang("", "NV01", gio(1, 0), 25000);
            ch.huyHoaDon(hd.getMa());
            dung(ch.banHang("", "NV01", gio(1, 0), 25000).getMa().equals("HD000002"), "Mã tiếp theo");
        });
        ca("34 Danh sách chỉ đọc và chi tiết không bị thêm/xóa từ ngoài", () -> {
            CuaHang ch = moi(); HoaDon hd = ch.banHang("", "NV01", gio(1, 0), 25000);
            loi(UnsupportedOperationException.class, () -> ch.getSanPham().tatCa().clear());
            loi(UnsupportedOperationException.class, () -> ch.getHoaDon().clear());
            loi(UnsupportedOperationException.class, () -> ch.getNhanVien().clear());
            loi(UnsupportedOperationException.class, () -> hd.getChiTiet().clear());
        });
        ca("35 Tiền vượt int vẫn đúng nhờ long", () -> {
            CuaHang ch = moi();
            ch.getSanPham().them(new DoUong("BIG", "Lớn", QuyTac.GIA_TOI_DA,
                    QuyTac.TON_KHO_TOI_DA, 0, LocalDate.MAX));
            HoaDon hd = ch.banHang("", "NV01", Map.of("BIG", QuyTac.TON_KHO_TOI_DA), 10_000_000_000_000L);
            dung(hd.tongTien() == 10_000_000_000_000L, "Không tràn int");
        });
        ca("36 Nhập sai số, số vượt long và EOF không lặp vô hạn", () -> {
            NhapLieu nhap = nhapTu("abc\n99999999999999999999999\n-1\n11\n3\n");
            dung(nhap.soNguyen("", 0, 10) == 3, "Bỏ qua các lần nhập sai");
            loi(NoSuchElementException.class, () -> nhap.dong(""));
        });
        ca("37 Ngày thực, năm nhuận, SĐT và xác nhận", () -> {
            NhapLieu nhap = nhapTu("31/02/2030\n29/02/2030\n29/02/2032\n123\n0900000001\nmaybe\ny\n");
            dung(nhap.ngay("").equals(LocalDate.of(2032, 2, 29)), "Ngày nhuận hợp lệ");
            dung(nhap.soDienThoai("").equals("0900000001"), "Nhập lại SĐT");
            dung(nhap.xacNhan(""), "Nhập lại xác nhận");
        });
        ca("38 Lưu/đọc UTF-8, snapshot, trạng thái hủy và mã tiếp theo", KiemThu::thuLuuDoc);
        ca("39 Lưu/đọc cửa hàng trống", KiemThu::thuFileTrong);
        ca("40 Từ chối file thiếu trường, sai loại/số/ngày/trạng thái/tham chiếu", KiemThu::thuFileHong);
        ca("41 Ghi thất bại không làm mất file chính", KiemThu::thuGhiThatBai);
        ca("42 Đọc file không tồn tại trả lỗi IOException", () -> {
            try {
                Path thuMuc = Files.createTempDirectory("caphe-test-thieu-");
                try { docPhaiLoi(thuMuc.resolve("khong-co.txt")); }
                finally { xoaTam(thuMuc); }
            } catch (IOException e) { throw new AssertionError(e); }
        });
        ca("43 HoaDon sao chép danh sách và chặn chi tiết trùng", () -> {
            List<ChiTietHoaDon> ct = new ArrayList<>();
            ct.add(new ChiTietHoaDon("DU01", "A", 1, 10));
            HoaDon hd = new HoaDon("HD1", NGAY.atStartOfDay(), "", "Vãng lai", "NV01", "An", ct, 10, false);
            ct.clear(); dung(hd.getChiTiet().size() == 1, "Sao chép danh sách đầu vào");
            ct.add(new ChiTietHoaDon("DU01", "A", 1, 10)); ct.add(new ChiTietHoaDon("du01", "B", 1, 10));
            loi(IllegalArgumentException.class, () -> new HoaDon("HD2", NGAY.atStartOfDay(), "", "Vãng lai", "NV01", "An", ct, 20, false));
        });
        System.out.printf("%nKẾT QUẢ: %d/%d ca đạt; %d phép kiểm tra; %d ca lỗi.%n", dat, dat + hong, soKiemTra, hong);
        if (hong > 0) System.exit(1);
    }

    private static NhapLieu nhapTu(String duLieu) {
        return new NhapLieu(new ByteArrayInputStream(duLieu.getBytes(StandardCharsets.UTF_8)),
                new PrintStream(new ByteArrayOutputStream(), true, StandardCharsets.UTF_8));
    }

    private static void thuLuuDoc() {
        try {
            Path thuMuc = Files.createTempDirectory("caphe-test-luudoc-");
            try {
                Path tep = thuMuc.resolve("du lieu tieng Viet.txt");
                CuaHang ch = moi();
                ch.getSanPham().sua(new DoUong("DU01", "Cà phê | sữa = đậm \\ vị", 25000, 10, 0, LocalDate.MAX));
                HoaDon a = ch.banHang("KH01", "NV01", gio(1, 1), 50000);
                HoaDon b = ch.banHang("", "NV01", gio(1, 0), 30000); ch.huyHoaDon(b.getMa());
                ch.getSanPham().sua(new DoUong("DU01", "Tên hiện tại", 45000, 9, 0, LocalDate.MAX));
                LuuTru.ghi(ch, tep); LuuTru.ghi(ch, tep);
                dung(Files.exists(thuMuc.resolve(tep.getFileName() + ".bak")), "Có bản sao lưu");
                CuaHang lai = LuuTru.doc(tep);
                dung(lai.getHoaDon().size() == 2, "Đủ hóa đơn");
                dung(lai.batBuocCoSanPham("DU01").getTonKho() == 9, "Không trừ/hoàn kho lần nữa");
                dung(lai.tongDoanhThu() == 43000, "Doanh thu phục hồi");
                dung(lai.timHoaDon(b.getMa()).isDaHuy(), "Giữ trạng thái hủy");
                dung(lai.timHoaDon(a.getMa()).getChiTiet().get(0).getTenSanPham().equals("Cà phê | sữa = đậm \\ vị"), "Tên snapshot và ký tự đặc biệt");
                dung(lai.timHoaDon(a.getMa()).getChiTiet().get(0).getDonGia() == 25000, "Giá snapshot");
                dung(lai.timHoaDon(a.getMa()).getNgay().equals(a.getNgay()), "Thời điểm snapshot");
                dung(lai.banHang("", "NV01", gio(1, 0), 50000).getMa().equals("HD000003"), "Mã không trùng sau đọc");
                dung(lai.huyHoaDon(a.getMa()) == 43000, "Hủy hóa đơn đã đọc");
                dung(lai.batBuocCoSanPham("TP01").getTonKho() == 10, "Hoàn kho sau đọc");
                dung(lai.tongDoanhThu() == 45000, "Doanh thu sau hủy");
            } finally { xoaTam(thuMuc); }
        } catch (IOException e) { throw new AssertionError(e); }
    }

    private static void thuFileTrong() {
        try {
            Path thuMuc = Files.createTempDirectory("caphe-test-trong-");
            try {
                Path tep = thuMuc.resolve("a.txt"); LuuTru.ghi(new CuaHang(), tep);
                CuaHang ch = LuuTru.doc(tep);
                dung(ch.getSanPham().tatCa().isEmpty() && ch.getHoaDon().isEmpty(), "Đọc đúng cửa hàng trống");
                dung(ch.tongDoanhThu() == 0, "Doanh thu 0");
            } finally { xoaTam(thuMuc); }
        } catch (IOException e) { throw new AssertionError(e); }
    }

    private static void thuFileHong() {
        try {
            Path thuMuc = Files.createTempDirectory("caphe-test-hong-");
            try {
                Path tep = thuMuc.resolve("a.txt"); CuaHang ch = moi();
                ch.banHang("KH01", "NV01", gio(1, 0), 25000); LuuTru.ghi(ch, tep);
                Properties goc = new Properties();
                try (Reader r = Files.newBufferedReader(tep, StandardCharsets.UTF_8)) { goc.load(r); }
                String[][] bienThe = {
                    {"phienBan", "2"}, {"sanPham.soLuong", "-1"}, {"sanPham.soLuong", "abc"},
                    {"sanPham.0.loai", "KHAC"}, {"sanPham.0.tonKho", "-5"},
                    {"sanPham.0.hanSuDung", "2030-02-31"}, {"sanPham.1.ma", "DU01"},
                    {"nhanVien.0.sdt", "123"}, {"hoaDon.0.daHuy", "yes"},
                    {"hoaDon.0.maNhanVien", "NV99"}, {"hoaDon.0.maKhach", "KH99"},
                    {"hoaDon.0.chiTiet.0.ma", "SP99"}, {"hoaDon.0.chiTiet.0.donGia", "-1"},
                    {"hoaDon.0.chiTiet.0.soLuong", "0"}, {"hoaDon.0.tienKhachDua", "1"}
                };
                for (String[] sua : bienThe) {
                    Properties p = new Properties(); p.putAll(goc); p.setProperty(sua[0], sua[1]);
                    ghiProperties(p, tep); byte[] truoc = Files.readAllBytes(tep);
                    docPhaiLoi(tep);
                    dung(Arrays.equals(truoc, Files.readAllBytes(tep)), "Đọc lỗi không ghi đè file");
                }
                Properties thieu = new Properties(); thieu.putAll(goc); thieu.remove("sanPham.0.ten");
                ghiProperties(thieu, tep); docPhaiLoi(tep);
                Files.writeString(tep, "file da bi cat ngan", StandardCharsets.UTF_8); docPhaiLoi(tep);
                Files.writeString(tep, "phienBan=\\uXXXX", StandardCharsets.UTF_8); docPhaiLoi(tep);
                dung(ch.tongDoanhThu() == 25000 && ch.batBuocCoSanPham("DU01").getTonKho() == 9,
                        "Đọc lỗi không ảnh hưởng đối tượng đang dùng");
            } finally { xoaTam(thuMuc); }
        } catch (IOException e) { throw new AssertionError(e); }
    }

    private static void thuGhiThatBai() {
        try {
            Path thuMuc = Files.createTempDirectory("caphe-test-ghi-");
            try {
                Path tep = thuMuc.resolve("a.txt"); CuaHang ch = moi(); LuuTru.ghi(ch, tep);
                byte[] truoc = Files.readAllBytes(tep);
                Path canTro = thuMuc.resolve("a.txt.bak"); Files.createDirectory(canTro);
                Files.writeString(canTro.resolve("chan.txt"), "Ngăn ghi đè bản sao lưu");
                ch.nhapKho("DU01", 1);
                boolean coLoi = false;
                try { LuuTru.ghi(ch, tep); } catch (IOException e) { coLoi = true; }
                dung(coLoi, "Báo ghi thất bại");
                dung(Arrays.equals(truoc, Files.readAllBytes(tep)), "File chính giữ nguyên");
                try (Stream<Path> cacTep = Files.list(thuMuc)) {
                    dung(cacTep.noneMatch(p -> p.toString().endsWith(".tmp")), "Dọn file tạm");
                }
            } finally { xoaTam(thuMuc); }
        } catch (IOException e) { throw new AssertionError(e); }
    }

    private static void ghiProperties(Properties p, Path tep) throws IOException {
        try (Writer w = Files.newBufferedWriter(tep, StandardCharsets.UTF_8)) { p.store(w, "test"); }
    }

    private static void docPhaiLoi(Path tep) {
        boolean coLoi = false;
        try { LuuTru.doc(tep); } catch (IOException e) { coLoi = true; }
        dung(coLoi, "File sai phải bị từ chối: " + tep.getFileName());
    }

    private static void xoaTam(Path thuMuc) throws IOException {
        // Chỉ xóa thư mục tạm vừa tạo bởi ca kiểm thử, không đụng data/cuahang.txt.
        try (Stream<Path> paths = Files.walk(thuMuc)) {
            for (Path p : paths.sorted(Comparator.reverseOrder()).toList()) Files.deleteIfExists(p);
        }
    }
}

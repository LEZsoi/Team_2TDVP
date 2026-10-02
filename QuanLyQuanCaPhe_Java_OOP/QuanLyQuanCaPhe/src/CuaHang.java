import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public final class CuaHang {
    private final DanhSachSanPham sanPham = new DanhSachSanPham();
    private final ArrayList<KhachHang> khachHang = new ArrayList<>();
    private final ArrayList<NhanVien> nhanVien = new ArrayList<>();
    private final ArrayList<HoaDon> hoaDon = new ArrayList<>();
    private final Clock dongHo;

    public CuaHang() { this(Clock.systemDefaultZone()); }

    // Truyền đồng hồ cố định trong KiemThu để kiểm tra ranh giới ngày.
    CuaHang(Clock dongHo) { this.dongHo = Objects.requireNonNull(dongHo); }

    public DanhSachSanPham getSanPham() { return sanPham; }
    public LocalDate getNgayHienTai() { return LocalDate.now(dongHo); }
    public List<KhachHang> getKhachHang() {
        return Collections.unmodifiableList(new ArrayList<>(khachHang));
    }
    public List<NhanVien> getNhanVien() {
        return Collections.unmodifiableList(new ArrayList<>(nhanVien));
    }
    public List<HoaDon> getHoaDon() {
        return Collections.unmodifiableList(new ArrayList<>(hoaDon));
    }

    public SanPham batBuocCoSanPham(String ma) {
        SanPham ketQua = sanPham.timTheoMa(ma);
        QuyTac.yeuCau(ketQua != null, "Không tìm thấy sản phẩm: " + ma);
        return ketQua;
    }

    public void nhapKho(String ma, int soLuong) { batBuocCoSanPham(ma).nhapKho(soLuong); }

    public void xoaSanPham(String ma) {
        SanPham sp = batBuocCoSanPham(ma);
        for (HoaDon hd : hoaDon) {
            for (ChiTietHoaDon ct : hd.getChiTiet()) {
                QuyTac.yeuCau(!ct.getMaSanPham().equals(sp.getMa()),
                        "Sản phẩm đã có trong lịch sử hóa đơn, không được xóa.");
            }
        }
        sanPham.xoa(sp.getMa());
    }

    public KhachHang timKhachHang(String ma) {
        String maChuan = QuyTac.chuanHoaMa(ma);
        for (KhachHang kh : khachHang) if (kh.getMa().equals(maChuan)) return kh;
        return null;
    }

    public NhanVien timNhanVien(String ma) {
        String maChuan = QuyTac.chuanHoaMa(ma);
        for (NhanVien nv : nhanVien) if (nv.getMa().equals(maChuan)) return nv;
        return null;
    }

    public void themKhachHang(KhachHang kh) {
        Objects.requireNonNull(kh);
        QuyTac.yeuCau(timKhachHang(kh.getMa()) == null, "Mã khách hàng đã tồn tại.");
        khachHang.add(kh);
    }

    public void themNhanVien(NhanVien nv) {
        Objects.requireNonNull(nv);
        QuyTac.yeuCau(timNhanVien(nv.getMa()) == null, "Mã nhân viên đã tồn tại.");
        nhanVien.add(nv);
    }

    public void suaKhachHang(String ma, String ten, String soDienThoai) {
        KhachHang kh = timKhachHang(ma);
        QuyTac.yeuCau(kh != null, "Không tìm thấy khách hàng.");
        kh.capNhat(ten, soDienThoai);
    }

    public void suaNhanVien(String ma, String ten, String soDienThoai) {
        NhanVien nv = timNhanVien(ma);
        QuyTac.yeuCau(nv != null, "Không tìm thấy nhân viên.");
        nv.capNhat(ten, soDienThoai);
    }

    public void xoaKhachHang(String ma) {
        KhachHang kh = timKhachHang(ma);
        QuyTac.yeuCau(kh != null, "Không tìm thấy khách hàng.");
        for (HoaDon hd : hoaDon) {
            QuyTac.yeuCau(!hd.getMaKhachHang().equals(kh.getMa()), "Khách đã có hóa đơn, không được xóa.");
        }
        khachHang.remove(kh);
    }

    public void xoaNhanVien(String ma) {
        NhanVien nv = timNhanVien(ma);
        QuyTac.yeuCau(nv != null, "Không tìm thấy nhân viên.");
        for (HoaDon hd : hoaDon) {
            QuyTac.yeuCau(!hd.getMaNhanVien().equals(nv.getMa()), "Nhân viên đã lập hóa đơn, không được xóa.");
        }
        nhanVien.remove(nv);
    }

    public HoaDon timHoaDon(String ma) {
        String maChuan = QuyTac.chuanHoaMa(ma);
        for (HoaDon hd : hoaDon) if (hd.getMa().equals(maChuan)) return hd;
        return null;
    }

    public List<ChiTietHoaDon> kiemTraGioHang(Map<String, Integer> gio) {
        return kiemTraGioHang(gio, getNgayHienTai());
    }

    private List<ChiTietHoaDon> kiemTraGioHang(Map<String, Integer> gio, LocalDate ngay) {
        QuyTac.yeuCau(gio != null && !gio.isEmpty(), "Giỏ hàng đang trống.");
        ArrayList<ChiTietHoaDon> ketQua = new ArrayList<>();
        Set<String> maDaCo = new HashSet<>();
        for (Map.Entry<String, Integer> muc : gio.entrySet()) {
            SanPham sp = batBuocCoSanPham(muc.getKey());
            QuyTac.yeuCau(maDaCo.add(sp.getMa()), "Mã bị lặp sau chuẩn hóa; hãy gộp số lượng.");
            Integer sl = muc.getValue();
            QuyTac.yeuCau(sl != null && sl > 0, "Số lượng mua phải lớn hơn 0.");
            QuyTac.yeuCau(sp.duocBan(ngay), "Sản phẩm đã hết hạn: " + sp.getMa());
            QuyTac.yeuCau(sl <= sp.getTonKho(), "Không đủ tồn kho: " + sp.getMa());
            ketQua.add(new ChiTietHoaDon(sp, sl));
        }
        return Collections.unmodifiableList(ketQua);
    }

    public long tinhTienGioHang(Map<String, Integer> gio) {
        long tong = 0;
        for (ChiTietHoaDon ct : kiemTraGioHang(gio)) tong = Math.addExact(tong, ct.thanhTien());
        return tong;
    }

    public HoaDon banHang(String maKhach, String maNhanVien, Map<String, Integer> gio,
                          long tienKhachDua) {
        QuyTac.yeuCau(maKhach != null, "Dùng chuỗi rỗng cho khách vãng lai.");
        NhanVien nv = timNhanVien(maNhanVien);
        QuyTac.yeuCau(nv != null, "Không tìm thấy nhân viên lập hóa đơn.");
        KhachHang kh = maKhach.isBlank() ? null : timKhachHang(maKhach);
        QuyTac.yeuCau(maKhach.isBlank() || kh != null, "Không tìm thấy khách hàng.");
        LocalDateTime thoiDiem = LocalDateTime.now(dongHo);
        List<ChiTietHoaDon> chiTiet = kiemTraGioHang(gio, thoiDiem.toLocalDate());
        String maMoi = taoMaHoaDon();
        // Constructor kiểm tra cả số tiền trước khi bất kỳ tồn kho nào bị thay đổi.
        HoaDon hd = new HoaDon(maMoi, thoiDiem, kh == null ? "" : kh.getMa(),
                kh == null ? "Khách vãng lai" : kh.getTen(), nv.getMa(), nv.getTen(),
                chiTiet, tienKhachDua, false);
        for (ChiTietHoaDon ct : chiTiet) {
            batBuocCoSanPham(ct.getMaSanPham()).xuatKho(ct.getSoLuong());
        }
        hoaDon.add(hd);
        return hd;
    }

    private String taoMaHoaDon() {
        long so = (long) hoaDon.size() + 1;
        String ma;
        do { ma = String.format(Locale.ROOT, "HD%06d", so++); }
        while (timHoaDon(ma) != null);
        return ma;
    }

    /** Hủy toàn bộ hóa đơn, trả lại tổng tiền hàng đã thu (không trả lại tiền thừa lần nữa). */
    public long huyHoaDon(String ma) {
        HoaDon hd = timHoaDon(ma);
        QuyTac.yeuCau(hd != null, "Không tìm thấy hóa đơn.");
        QuyTac.yeuCau(!hd.isDaHuy(), "Hóa đơn đã hủy trước đó.");
        // Kiểm tra TẤT CẢ các dòng trước khi hoàn kho để tránh thay đổi dở dang.
        for (ChiTietHoaDon ct : hd.getChiTiet()) {
            SanPham sp = batBuocCoSanPham(ct.getMaSanPham());
            QuyTac.yeuCau((long) sp.getTonKho() + ct.getSoLuong() <= QuyTac.TON_KHO_TOI_DA,
                    "Không thể hủy: hoàn kho vượt giới hạn tại " + sp.getMa());
        }
        for (ChiTietHoaDon ct : hd.getChiTiet()) {
            batBuocCoSanPham(ct.getMaSanPham()).nhapKho(ct.getSoLuong());
        }
        hd.danhDauHuy();
        return hd.tongTien();
    }

    public long doanhThu(LocalDate tuNgay, LocalDate denNgay) {
        Objects.requireNonNull(tuNgay);
        Objects.requireNonNull(denNgay);
        QuyTac.yeuCau(!tuNgay.isAfter(denNgay), "Ngày bắt đầu phải <= ngày kết thúc.");
        long tong = 0;
        for (HoaDon hd : hoaDon) {
            LocalDate ngay = hd.getNgay().toLocalDate();
            if (!hd.isDaHuy() && !ngay.isBefore(tuNgay) && !ngay.isAfter(denNgay)) {
                tong = Math.addExact(tong, hd.tongTien());
            }
        }
        return tong;
    }

    public long tongDoanhThu() { return doanhThu(LocalDate.MIN, LocalDate.MAX); }

    public Map<String, Long> soLuongDaBan() {
        Map<String, Long> ketQua = new LinkedHashMap<>();
        for (HoaDon hd : hoaDon) if (!hd.isDaHuy()) {
            for (ChiTietHoaDon ct : hd.getChiTiet()) {
                long cu = ketQua.getOrDefault(ct.getMaSanPham(), 0L);
                ketQua.put(ct.getMaSanPham(), Math.addExact(cu, ct.getSoLuong()));
            }
        }
        return Collections.unmodifiableMap(ketQua);
    }

    /** Chỉ dùng để phục hồi dữ liệu; tồn kho trong file đã là tồn sau giao dịch. */
    void napHoaDon(HoaDon hd) {
        Objects.requireNonNull(hd);
        QuyTac.yeuCau(timHoaDon(hd.getMa()) == null, "Trùng mã hóa đơn trong file.");
        QuyTac.yeuCau(timNhanVien(hd.getMaNhanVien()) != null, "Hóa đơn tham chiếu nhân viên không tồn tại.");
        QuyTac.yeuCau(hd.getMaKhachHang().isEmpty() || timKhachHang(hd.getMaKhachHang()) != null,
                "Hóa đơn tham chiếu khách hàng không tồn tại.");
        for (ChiTietHoaDon ct : hd.getChiTiet()) batBuocCoSanPham(ct.getMaSanPham());
        hoaDon.add(hd);
    }

    public static CuaHang duLieuMau() {
        CuaHang ch = new CuaHang();
        LocalDate homNay = ch.getNgayHienTai();
        ch.sanPham.them(new DoUong("DU01", "Cà phê sữa", 25000, 40, 0, homNay.plusDays(120)));
        ch.sanPham.them(new DoUong("DU02", "Trà đào", 30000, 30, 10, homNay.plusDays(90)));
        ch.sanPham.them(new ThucPham("TP01", "Bánh mì", 20000, 20, 0, homNay.plusDays(7)));
        ch.sanPham.them(new ThucPham("TP02", "Bánh ngọt hết hạn", 15000, 5, 0, homNay.minusDays(1)));
        ch.sanPham.them(new DoUong("DU03", "Nước suối hết hàng", 10000, 0, 0, homNay.plusDays(365)));
        ch.themKhachHang(new KhachHang("KH01", "Mai Linh", "0900000001"));
        ch.themKhachHang(new KhachHang("KH02", "Minh Anh", "0900000002"));
        ch.themNhanVien(new NhanVien("NV01", "Nguyễn An", "0900000003"));
        ch.themNhanVien(new NhanVien("NV02", "Trần Bình", "0900000004"));
        return ch;
    }
}

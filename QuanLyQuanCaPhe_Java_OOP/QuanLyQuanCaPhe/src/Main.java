import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;


public final class Main {
    private final NhapLieu nhap = new NhapLieu(System.in, System.out);
    private final Path tepDuLieu;
    private CuaHang cuaHang;
    private boolean chuaLuu;

    private Main(Path tepDuLieu) { this.tepDuLieu = tepDuLieu; }

    public static void main(String[] args) {
        if (args.length != 0 && !(args.length == 2 && args[0].equals("--data"))) {
            System.out.println("Cách chạy: java -cp out Main [--data duong/dan/cuahang.txt]");
            return;
        }
        Path tep = Path.of(args.length == 0 ? "data/cuahang.txt" : args[1]);
        new Main(tep).chay();
    }

    private void chay() {
        System.out.println("\n" + QuyTac.TEN_CUA_HANG + " - QUẢN LÝ CONSOLE");
        try {
            if (Files.exists(tepDuLieu)) cuaHang = LuuTru.doc(tepDuLieu);
            else {
                cuaHang = CuaHang.duLieuMau();
                chuaLuu = true;
                System.out.println("Lần đầu chạy: tạo dữ liệu mẫu. NV01/NV02; KH01/KH02.");
                luu();
            }
        } catch (IOException e) {
            System.out.println("Không mở được dữ liệu: " + e.getMessage());
            System.out.println("Chương trình dừng và giữ nguyên file. Xem hướng dẫn khôi phục .bak.");
            return;
        }
        System.out.println("File dữ liệu: " + tepDuLieu.toAbsolutePath().normalize());
        try {
            boolean tiepTuc = true;
            while (tiepTuc) {
                System.out.println("\n========== MENU CHÍNH ==========");
                System.out.println("1. Sản phẩm và thực đơn\n2. Nhập kho\n3. Khách hàng\n4. Nhân viên");
                System.out.println("5. Bán hàng\n6. Hóa đơn và hủy hóa đơn\n7. Thống kê doanh thu");
                System.out.println("8. Lưu dữ liệu\n9. Đọc lại dữ liệu từ file\n0. Thoát");
                if (chuaLuu) System.out.println("Có thay đổi CHƯA LƯU được vào file.");
                int chon = nhap.soNguyen("Chọn: ", 0, 9);
                try {
                    switch (chon) {
                        case 1: menuSanPham(); break;
                        case 2: nhapKho(); break;
                        case 3: menuNguoi(true); break;
                        case 4: menuNguoi(false); break;
                        case 5: banHang(); break;
                        case 6: menuHoaDon(); break;
                        case 7: thongKe(); break;
                        case 8: luu(); break;
                        case 9:
                            if (nhap.xacNhan("Đọc lại file và bỏ mọi thay đổi chưa lưu?")) {
                                CuaHang duLieuMoi = LuuTru.doc(tepDuLieu);
                                cuaHang = duLieuMoi; // Chỉ thay khi đã đọc xong toàn bộ.
                                chuaLuu = false;
                                System.out.println("Đã đọc dữ liệu thành công.");
                            }
                            break;
                        case 0:
                            if (chuaLuu) luu();
                            tiepTuc = chuaLuu && !nhap.xacNhan("Lưu thất bại. Vẫn thoát và bỏ thay đổi chưa lưu?");
                            break;
                        default: break;
                    }
                } catch (IllegalArgumentException | ArithmeticException e) {
                    System.out.println("Không thực hiện: " + e.getMessage());
                } catch (IOException e) {
                    System.out.println("Lỗi file: " + e.getMessage() + ". Dữ liệu đang dùng được giữ nguyên.");
                }
            }
        } catch (NoSuchElementException e) {
            System.out.println("\nĐã kết thúc luồng nhập.");
            if (chuaLuu) luu();
        }
        System.out.println("Đã kết thúc chương trình.");
    }

    private void sauThayDoi() {
        chuaLuu = true;
        luu();
    }

    private void luu() {
        try {
            LuuTru.ghi(cuaHang, tepDuLieu);
            chuaLuu = false;
            System.out.println("Đã lưu dữ liệu.");
        } catch (IOException e) {
            System.out.println("Chưa lưu được: " + e.getMessage());
            System.out.println("Thay đổi vẫn ở bộ nhớ. Kiểm tra đường dẫn/quyền ghi rồi chọn 8 để thử lại.");
        }
    }

    private void menuSanPham() {
        System.out.println("\n1. Xem tất cả  2. Thêm  3. Sửa  4. Xóa");
        System.out.println("5. Tìm mã  6. Tìm tên  7. Lọc loại  8. Lọc trạng thái  0. Quay lại");
        DanhSachSanPham ds = cuaHang.getSanPham();
        switch (nhap.soNguyen("Chọn: ", 0, 8)) {
            case 1: inSanPham(ds.tatCa()); break;
            case 2:
                ds.them(nhapSanPham(null));
                sauThayDoi();
                break;
            case 3:
                SanPham cu = cuaHang.batBuocCoSanPham(nhap.ma("Mã cần sửa: "));
                inSanPham(List.of(cu));
                System.out.println("Nhập lại các thông tin; giữ nguyên mã, loại và tồn kho.");
                ds.sua(nhapSanPham(cu));
                sauThayDoi();
                break;
            case 4:
                String ma = nhap.ma("Mã cần xóa: ");
                if (nhap.xacNhan("Xóa sản phẩm " + ma + "?")) {
                    cuaHang.xoaSanPham(ma);
                    sauThayDoi();
                }
                break;
            case 5:
                SanPham sp = ds.timTheoMa(nhap.ma("Mã tìm: "));
                inSanPham(sp == null ? List.of() : List.of(sp));
                break;
            case 6: inSanPham(ds.timTheoTen(nhap.chuoi("Tên hoặc một phần tên: "))); break;
            case 7:
                int loai = nhap.soNguyen("1. Đồ uống  2. Thực phẩm: ", 1, 2);
                inSanPham(ds.locTheoLoai(loai == 1 ? "DO_UONG" : "THUC_PHAM"));
                break;
            case 8:
                int tt = nhap.soNguyen("1. Còn bán  2. Hết hạn  3. Hết hàng: ", 1, 3);
                inSanPham(ds.locTheoTrangThai(tt, cuaHang.getNgayHienTai()));
                break;
            default: break;
        }
    }

    private SanPham nhapSanPham(SanPham cu) {
        String ma = cu == null ? nhap.ma("Mã: ") : cu.getMa();
        int loai = cu == null ? nhap.soNguyen("1. Đồ uống  2. Thực phẩm: ", 1, 2)
                : (cu instanceof DoUong ? 1 : 2);
        String ten = nhap.chuoi("Tên: ");
        long gia = nhap.soNguyenDai("Giá gốc (đồng): ", 1, QuyTac.GIA_TOI_DA);
        int ton = cu == null ? nhap.soNguyen("Tồn kho ban đầu: ", 0, QuyTac.TON_KHO_TOI_DA) : cu.getTonKho();
        int giam = nhap.soNguyen("Giảm giá (%): ", 0, 100);
        LocalDate han = nhap.ngay("Hạn sử dụng (dd/MM/yyyy): ");
        return loai == 1 ? new DoUong(ma, ten, gia, ton, giam, han)
                : new ThucPham(ma, ten, gia, ton, giam, han);
    }

    private void nhapKho() {
        inSanPham(cuaHang.getSanPham().tatCa());
        String ma = nhap.ma("Mã nhập thêm: ");
        SanPham sp = cuaHang.batBuocCoSanPham(ma);
        System.out.println("Lô hiện tại có hạn " + sp.getHanSuDung().format(QuyTac.DINH_DANG_NGAY)
                + ". Hàng khác hạn dùng phải tạo mã mới.");
        int sl = nhap.soNguyen("Số lượng nhập thêm: ", 1, QuyTac.TON_KHO_TOI_DA);
        cuaHang.nhapKho(ma, sl);
        System.out.println("Tồn kho mới: " + sp.getTonKho());
        sauThayDoi();
    }

    private void menuNguoi(boolean laKhach) {
        System.out.println(laKhach ? "\nQUẢN LÝ KHÁCH HÀNG" : "\nQUẢN LÝ NHÂN VIÊN");
        System.out.println("1. Xem  2. Thêm  3. Sửa  4. Xóa  5. Tìm mã/tên  0. Quay lại");
        int chon = nhap.soNguyen("Chọn: ", 0, 5);
        if (chon == 0) return;
        List<? extends Nguoi> ds = laKhach ? cuaHang.getKhachHang() : cuaHang.getNhanVien();
        if (chon == 1 || chon == 5) {
            String khoa = chon == 5 ? QuyTac.khoaTimKiem(nhap.chuoi("Từ khóa mã/tên: ")) : "";
            int dem = 0;
            for (Nguoi nguoi : ds) {
                if (QuyTac.khoaTimKiem(nguoi.getMa()).contains(khoa)
                        || QuyTac.khoaTimKiem(nguoi.getTen()).contains(khoa)) {
                    System.out.printf("%-12s | %-30s | %s%n", nguoi.getMa(), nguoi.getTen(), nguoi.getSoDienThoai());
                    dem++;
                }
            }
            if (dem == 0) System.out.println("Không có dữ liệu phù hợp.");
            return;
        }
        String ma = nhap.ma("Mã: ");
        if (chon == 4) {
            if (!nhap.xacNhan("Xóa " + ma + "?")) return;
            if (laKhach) cuaHang.xoaKhachHang(ma); else cuaHang.xoaNhanVien(ma);
        } else {
            String ten = nhap.chuoi("Họ tên: ");
            String sdt = nhap.soDienThoai("Số điện thoại: ");
            if (chon == 2) {
                if (laKhach) cuaHang.themKhachHang(new KhachHang(ma, ten, sdt));
                else cuaHang.themNhanVien(new NhanVien(ma, ten, sdt));
            } else {
                if (laKhach) cuaHang.suaKhachHang(ma, ten, sdt); else cuaHang.suaNhanVien(ma, ten, sdt);
            }
        }
        sauThayDoi();
    }

    private void banHang() {
        Map<String, Integer> gio = new LinkedHashMap<>();
        inSanPham(cuaHang.getSanPham().locTheoTrangThai(1, cuaHang.getNgayHienTai()));
        while (true) {
            System.out.println("\nGIỎ HÀNG: 1. Thêm món  2. Đổi số lượng  3. Xóa món  4. Xem giỏ  5. Thanh toán  0. Bỏ giỏ");
            int chon = nhap.soNguyen("Chọn: ", 0, 5);
            if (chon == 0) {
                if (gio.isEmpty() || nhap.xacNhan("Bỏ giỏ hàng chưa thanh toán?")) return;
                continue;
            }
            try {
                if (chon == 1 || chon == 2) {
                    String ma = nhap.ma("Mã sản phẩm: ");
                    if (chon == 2) QuyTac.yeuCau(gio.containsKey(ma), "Món chưa có trong giỏ.");
                    int sl = nhap.soNguyen(chon == 1 ? "Số lượng thêm: " : "Số lượng mới: ", 1, QuyTac.TON_KHO_TOI_DA);
                    Map<String, Integer> thu = new LinkedHashMap<>(gio);
                    thu.put(ma, chon == 1 ? Math.addExact(gio.getOrDefault(ma, 0), sl) : sl);
                    cuaHang.kiemTraGioHang(thu); // Giỏ cũ được giữ nguyên nếu số lượng mới sai.
                    gio = thu;
                    inGio(gio);
                } else if (chon == 3) {
                    String ma = nhap.ma("Mã cần bỏ: ");
                    QuyTac.yeuCau(gio.remove(ma) != null, "Món chưa có trong giỏ.");
                    inGio(gio);
                } else if (chon == 4) {
                    inGio(gio);
                } else {
                    QuyTac.yeuCau(!gio.isEmpty(), "Giỏ hàng đang trống.");
                    inGio(gio);
                    String nv = nhap.ma("Mã nhân viên (mẫu NV01): ");
                    String kh = nhap.dong("Mã khách (mẫu KH01; Enter = vãng lai): ");
                    long tien = nhap.soNguyenDai("Tiền khách đưa (đồng): ", 0, Long.MAX_VALUE);
                    if (!nhap.xacNhan("Xác nhận thanh toán?")) continue;
                    HoaDon hd = cuaHang.banHang(kh, nv, gio, tien);
                    inHoaDon(hd);
                    sauThayDoi();
                    return;
                }
            } catch (IllegalArgumentException | ArithmeticException e) {
                System.out.println("Không thực hiện: " + e.getMessage());
            }
        }
    }

    private void menuHoaDon() {
        System.out.println("\n1. Xem danh sách  2. Xem chi tiết  3. Hủy hóa đơn  0. Quay lại");
        int chon = nhap.soNguyen("Chọn: ", 0, 3);
        if (chon == 0) return;
        if (chon == 1) {
            if (cuaHang.getHoaDon().isEmpty()) System.out.println("Chưa có hóa đơn.");
            for (HoaDon hd : cuaHang.getHoaDon()) {
                System.out.printf("%s | %s | %s | %s%n", hd.getMa(),
                        hd.getNgay().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                        QuyTac.dinhDangTien(hd.tongTien()), hd.getTrangThai());
            }
            return;
        }
        String ma = nhap.ma("Mã hóa đơn: ");
        HoaDon hd = cuaHang.timHoaDon(ma);
        QuyTac.yeuCau(hd != null, "Không tìm thấy hóa đơn.");
        inHoaDon(hd);
        if (chon == 3 && nhap.xacNhan("Hủy toàn bộ hóa đơn và hoàn kho?")) {
            long hoan = cuaHang.huyHoaDon(ma);
            System.out.println("Đã hủy. Số tiền cần hoàn khách: " + QuyTac.dinhDangTien(hoan));
            sauThayDoi();
        }
    }

    private void thongKe() {
        System.out.println("\n1. Toàn bộ  2. Theo khoảng ngày  0. Quay lại");
        int chon = nhap.soNguyen("Chọn: ", 0, 2);
        if (chon == 0) return;
        LocalDate tu = chon == 1 ? LocalDate.MIN : nhap.ngay("Từ ngày (dd/MM/yyyy): ");
        LocalDate den = chon == 1 ? LocalDate.MAX : nhap.ngay("Đến ngày (dd/MM/yyyy): ");
        long doanhThu = cuaHang.doanhThu(tu, den);
        int daBan = 0, daHuy = 0;
        for (HoaDon hd : cuaHang.getHoaDon()) {
            LocalDate ngay = hd.getNgay().toLocalDate();
            if (!ngay.isBefore(tu) && !ngay.isAfter(den)) {
                if (hd.isDaHuy()) daHuy++; else daBan++;
            }
        }
        System.out.println("Hóa đơn còn hiệu lực: " + daBan + " | Đã hủy: " + daHuy);
        System.out.println("Doanh thu thực tế: " + QuyTac.dinhDangTien(doanhThu));
        System.out.println("Doanh thu tính theo ngày lập; hóa đơn đã hủy bị loại khỏi kỳ gốc.");
        if (chon == 1) {
            System.out.println("Số lượng bán ròng theo mã sản phẩm:");
            for (Map.Entry<String, Long> muc : cuaHang.soLuongDaBan().entrySet()) {
                System.out.println(muc.getKey() + ": " + muc.getValue());
            }
        }
    }

    private void inSanPham(List<SanPham> ds) {
        if (ds.isEmpty()) { System.out.println("Không có sản phẩm phù hợp."); return; }
        for (SanPham sp : ds) {
            System.out.printf("%s | %s | %s%n", sp.getMa(), sp.getTen(), sp.getLoai());
            System.out.printf("  Giá gốc: %s | Giảm: %d%% | Giá bán: %s | Tồn: %d%n",
                    QuyTac.dinhDangTien(sp.getGiaGoc()), sp.getPhanTramGiamGia(),
                    QuyTac.dinhDangTien(sp.giaBanThucTe()), sp.getTonKho());
            System.out.printf("  HSD: %s | %s%n", sp.getHanSuDung().format(QuyTac.DINH_DANG_NGAY),
                    sp.trangThai(cuaHang.getNgayHienTai()));
        }
    }

    private void inGio(Map<String, Integer> gio) {
        if (gio.isEmpty()) { System.out.println("Giỏ hàng đang trống."); return; }
        inChiTiet(cuaHang.kiemTraGioHang(gio));
        System.out.println("Tạm tính: " + QuyTac.dinhDangTien(cuaHang.tinhTienGioHang(gio)));
        System.out.println("Chưa thanh toán, chưa trừ kho.");
    }

    private void inChiTiet(List<ChiTietHoaDon> ds) {
        for (ChiTietHoaDon ct : ds) {
            System.out.printf("%s | %s | %d x %s = %s%n", ct.getMaSanPham(), ct.getTenSanPham(),
                    ct.getSoLuong(), QuyTac.dinhDangTien(ct.getDonGia()), QuyTac.dinhDangTien(ct.thanhTien()));
        }
    }

    private void inHoaDon(HoaDon hd) {
        System.out.println("\n======== HÓA ĐƠN " + hd.getMa() + " ========");
        System.out.println("Ngày: " + hd.getNgay().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
        System.out.println("Khách: " + hd.getTenKhachHang() + " | Nhân viên: " + hd.getTenNhanVien());
        inChiTiet(hd.getChiTiet());
        System.out.println("Tổng tiền: " + QuyTac.dinhDangTien(hd.tongTien()));
        System.out.println("Khách đưa lúc bán: " + QuyTac.dinhDangTien(hd.getTienKhachDua()));
        System.out.println("Tiền thừa lúc bán: " + QuyTac.dinhDangTien(hd.tienThua()));
        System.out.println("Trạng thái: " + hd.getTrangThai());
        if (hd.isDaHuy()) System.out.println("Hóa đơn này không được tính vào doanh thu.");
    }
}

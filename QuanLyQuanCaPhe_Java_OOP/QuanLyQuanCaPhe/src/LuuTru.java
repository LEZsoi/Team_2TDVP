import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


public final class LuuTru {
    private LuuTru() { }

    public static void ghi(CuaHang ch, Path tep) throws IOException {
        Properties p = new Properties();
        dat(p, "phienBan", 1);
        List<SanPham> sanPham = ch.getSanPham().tatCa();
        dat(p, "sanPham.soLuong", sanPham.size());
        for (int i = 0; i < sanPham.size(); i++) {
            SanPham sp = sanPham.get(i);
            String k = "sanPham." + i + ".";
            dat(p, k + "loai", sp.getLoai());
            dat(p, k + "ma", sp.getMa());
            dat(p, k + "ten", sp.getTen());
            dat(p, k + "giaGoc", sp.getGiaGoc());
            dat(p, k + "tonKho", sp.getTonKho());
            dat(p, k + "giamGia", sp.getPhanTramGiamGia());
            dat(p, k + "hanSuDung", sp.getHanSuDung());
        }
        ghiNguoi(p, "khachHang", ch.getKhachHang());
        ghiNguoi(p, "nhanVien", ch.getNhanVien());
        List<HoaDon> hoaDon = ch.getHoaDon();
        dat(p, "hoaDon.soLuong", hoaDon.size());
        for (int i = 0; i < hoaDon.size(); i++) {
            HoaDon hd = hoaDon.get(i);
            String k = "hoaDon." + i + ".";
            dat(p, k + "ma", hd.getMa());
            dat(p, k + "ngay", hd.getNgay());
            dat(p, k + "maKhach", hd.getMaKhachHang());
            dat(p, k + "tenKhach", hd.getTenKhachHang());
            dat(p, k + "maNhanVien", hd.getMaNhanVien());
            dat(p, k + "tenNhanVien", hd.getTenNhanVien());
            dat(p, k + "tienKhachDua", hd.getTienKhachDua());
            dat(p, k + "daHuy", hd.isDaHuy());
            dat(p, k + "chiTiet.soLuong", hd.getChiTiet().size());
            for (int j = 0; j < hd.getChiTiet().size(); j++) {
                ChiTietHoaDon ct = hd.getChiTiet().get(j);
                String c = k + "chiTiet." + j + ".";
                dat(p, c + "ma", ct.getMaSanPham());
                dat(p, c + "ten", ct.getTenSanPham());
                dat(p, c + "soLuong", ct.getSoLuong());
                dat(p, c + "donGia", ct.getDonGia());
            }
        }

        Path dich = tep.toAbsolutePath().normalize();
        Files.createDirectories(dich.getParent());
        Path tam = Files.createTempFile(dich.getParent(), "caphe-", ".tmp");
        try {
            try (Writer writer = Files.newBufferedWriter(tam, StandardCharsets.UTF_8)) {
                p.store(writer, "QUAN LY QUAN CA PHE - UTF-8 - phien ban 1");
            }
            // Nếu tạo bản sao lưu thất bại, chưa thay đổi file dữ liệu chính.
            if (Files.exists(dich)) {
                Files.copy(dich, dich.resolveSibling(dich.getFileName() + ".bak"),
                        StandardCopyOption.REPLACE_EXISTING);
            }
            try {
                Files.move(tam, dich, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(tam, dich, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(tam);
        }
    }

    /** Đọc vào đối tượng mới; chỉ người gọi mới quyết định thay trạng thái đang sử dụng. */
    public static CuaHang doc(Path tep) throws IOException {
        Properties p = new Properties();
        try (Reader reader = Files.newBufferedReader(tep, StandardCharsets.UTF_8)) {
            p.load(reader);
        } catch (IllegalArgumentException e) {
            throw new IOException("File text có escape không hợp lệ.", e);
        }
        try {
            QuyTac.yeuCau(lay(p, "phienBan").equals("1"), "Phiên bản file không được hỗ trợ.");
            CuaHang ch = new CuaHang();
            int soSP = dem(p, "sanPham.soLuong");
            for (int i = 0; i < soSP; i++) {
                String k = "sanPham." + i + ".";
                String loai = lay(p, k + "loai");
                String ma = lay(p, k + "ma");
                String ten = lay(p, k + "ten");
                long gia = soDai(p, k + "giaGoc");
                int ton = soNguyen(p, k + "tonKho");
                int giam = soNguyen(p, k + "giamGia");
                LocalDate han = LocalDate.parse(lay(p, k + "hanSuDung"));
                SanPham sp;
                if (loai.equals("THUC_PHAM")) sp = new ThucPham(ma, ten, gia, ton, giam, han);
                else if (loai.equals("DO_UONG")) sp = new DoUong(ma, ten, gia, ton, giam, han);
                else throw new IllegalArgumentException("Loại sản phẩm không hợp lệ tại " + k);
                ch.getSanPham().them(sp);
            }
            int soKH = dem(p, "khachHang.soLuong");
            for (int i = 0; i < soKH; i++) {
                String k = "khachHang." + i + ".";
                ch.themKhachHang(new KhachHang(lay(p, k + "ma"), lay(p, k + "ten"), lay(p, k + "sdt")));
            }
            int soNV = dem(p, "nhanVien.soLuong");
            for (int i = 0; i < soNV; i++) {
                String k = "nhanVien." + i + ".";
                ch.themNhanVien(new NhanVien(lay(p, k + "ma"), lay(p, k + "ten"), lay(p, k + "sdt")));
            }
            int soHD = dem(p, "hoaDon.soLuong");
            for (int i = 0; i < soHD; i++) {
                String k = "hoaDon." + i + ".";
                List<ChiTietHoaDon> chiTiet = new ArrayList<>();
                int soCT = dem(p, k + "chiTiet.soLuong");
                for (int j = 0; j < soCT; j++) {
                    String c = k + "chiTiet." + j + ".";
                    chiTiet.add(new ChiTietHoaDon(lay(p, c + "ma"), lay(p, c + "ten"),
                            soNguyen(p, c + "soLuong"), soDai(p, c + "donGia")));
                }
                String huy = lay(p, k + "daHuy");
                QuyTac.yeuCau(huy.equals("true") || huy.equals("false"), "Trạng thái hủy phải là true/false.");
                ch.napHoaDon(new HoaDon(lay(p, k + "ma"), LocalDateTime.parse(lay(p, k + "ngay")),
                        lay(p, k + "maKhach"), lay(p, k + "tenKhach"), lay(p, k + "maNhanVien"),
                        lay(p, k + "tenNhanVien"), chiTiet, soDai(p, k + "tienKhachDua"),
                        Boolean.parseBoolean(huy)));
            }
            return ch;
        } catch (RuntimeException e) {
            throw new IOException("Dữ liệu không hợp lệ: " + e.getMessage(), e);
        }
    }

    private static void ghiNguoi(Properties p, String nhom, List<? extends Nguoi> danhSach) {
        dat(p, nhom + ".soLuong", danhSach.size());
        for (int i = 0; i < danhSach.size(); i++) {
            Nguoi nguoi = danhSach.get(i);
            String k = nhom + "." + i + ".";
            dat(p, k + "ma", nguoi.getMa());
            dat(p, k + "ten", nguoi.getTen());
            dat(p, k + "sdt", nguoi.getSoDienThoai());
        }
    }

    private static void dat(Properties p, String khoa, Object giaTri) {
        p.setProperty(khoa, String.valueOf(giaTri));
    }
    private static String lay(Properties p, String khoa) {
        String giaTri = p.getProperty(khoa);
        QuyTac.yeuCau(giaTri != null, "Thiếu trường: " + khoa);
        return giaTri;
    }
    private static int soNguyen(Properties p, String khoa) {
        return Integer.parseInt(lay(p, khoa));
    }
    private static long soDai(Properties p, String khoa) {
        return Long.parseLong(lay(p, khoa));
    }
    private static int dem(Properties p, String khoa) {
        int so = soNguyen(p, khoa);
        QuyTac.yeuCau(so >= 0 && so <= 100_000, "Số bản ghi không hợp lệ tại " + khoa);
        return so;
    }
}

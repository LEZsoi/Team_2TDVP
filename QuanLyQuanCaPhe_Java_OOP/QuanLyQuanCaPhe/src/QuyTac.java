import java.text.Normalizer;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.Locale;


public final class QuyTac {
    public static final String TEN_CUA_HANG = "CÀ PHÊ SINH VIÊN";
    public static final long GIA_TOI_DA = 10_000_000L;
    public static final int TON_KHO_TOI_DA = 1_000_000;
    public static final int DO_DAI_TEN_TOI_DA = 120;
    public static final DateTimeFormatter DINH_DANG_NGAY =
            DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);

    private QuyTac() { } // Không cần tạo đối tượng tiện ích.

    public static void yeuCau(boolean dieuKien, String thongBao) {
        if (!dieuKien) throw new IllegalArgumentException(thongBao);
    }

    public static String chuanHoaChuoi(String chuoi) {
        yeuCau(chuoi != null, "Chuỗi không được null.");
        String ketQua = chuoi.strip().replaceAll("(?U)\\s+", " ");
        yeuCau(!ketQua.isEmpty(), "Nội dung không được để trống.");
        yeuCau(ketQua.length() <= DO_DAI_TEN_TOI_DA, "Nội dung tối đa 120 ký tự.");
        yeuCau(ketQua.codePoints().noneMatch(Character::isISOControl),
                "Nội dung không được chứa ký tự điều khiển.");
        return ketQua;
    }

    public static String chuanHoaMa(String ma) {
        yeuCau(ma != null, "Mã không được null.");
        String ketQua = ma.strip().toUpperCase(Locale.ROOT);
        yeuCau(ketQua.matches("[A-Z0-9][A-Z0-9_-]{0,19}"),
                "Mã gồm 1-20 ký tự A-Z, 0-9, _ hoặc -; bắt đầu bằng chữ/số.");
        return ketQua;
    }

    public static String kiemTraSoDienThoai(String so) {
        yeuCau(so != null, "Số điện thoại không được null.");
        String ketQua = so.strip();
        yeuCau(ketQua.matches("0[0-9]{9}"),
                "Số điện thoại phải có đúng 10 chữ số và bắt đầu bằng 0.");
        return ketQua;
    }

    public static void kiemTraGia(long gia) {
        yeuCau(gia >= 1 && gia <= GIA_TOI_DA, "Giá gốc phải từ 1 đến 10.000.000 đồng.");
    }

    public static void kiemTraTonKho(int ton) {
        yeuCau(ton >= 0 && ton <= TON_KHO_TOI_DA, "Tồn kho phải từ 0 đến 1.000.000.");
    }

    public static void kiemTraGiamGia(int giam) {
        yeuCau(giam >= 0 && giam <= 100, "Giảm giá phải từ 0 đến 100%.");
    }

    public static String dinhDangTien(long tien) {
        return String.format(Locale.forLanguageTag("vi-VN"), "%,d đ", tien);
    }

    public static LocalDate docNgay(String chuoi) {
        return LocalDate.parse(chuoi.strip(), DINH_DANG_NGAY);
    }

    public static String khoaTimKiem(String chuoi) {
        yeuCau(chuoi != null, "Từ khóa không được null.");
        return Normalizer.normalize(chuoi, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "").toLowerCase(Locale.ROOT)
                .replace('đ', 'd').strip();
    }
}

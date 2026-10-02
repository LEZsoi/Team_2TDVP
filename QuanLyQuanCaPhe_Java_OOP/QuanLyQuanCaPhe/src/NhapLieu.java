import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.NoSuchElementException;
import java.util.Scanner;


public final class NhapLieu {
    private final Scanner scanner;
    private final PrintStream out;

    public NhapLieu(InputStream in, PrintStream out) {
        this.scanner = new Scanner(in, StandardCharsets.UTF_8);
        this.out = out;
    }

    public String dong(String thongBao) {
        out.print(thongBao);
        out.flush();
        if (!scanner.hasNextLine()) throw new NoSuchElementException("Đã hết dữ liệu nhập.");
        return scanner.nextLine().strip();
    }

    public String chuoi(String thongBao) {
        while (true) {
            try { return QuyTac.chuanHoaChuoi(dong(thongBao)); }
            catch (IllegalArgumentException e) { out.println("Lỗi: " + e.getMessage()); }
        }
    }

    public String ma(String thongBao) {
        while (true) {
            try { return QuyTac.chuanHoaMa(dong(thongBao)); }
            catch (IllegalArgumentException e) { out.println("Lỗi: " + e.getMessage()); }
        }
    }

    public String soDienThoai(String thongBao) {
        while (true) {
            try { return QuyTac.kiemTraSoDienThoai(dong(thongBao)); }
            catch (IllegalArgumentException e) { out.println("Lỗi: " + e.getMessage()); }
        }
    }

    public long soNguyenDai(String thongBao, long min, long max) {
        QuyTac.yeuCau(min <= max, "Khoảng nhập không hợp lệ.");
        while (true) {
            try {
                long giaTri = Long.parseLong(dong(thongBao));
                QuyTac.yeuCau(giaTri >= min && giaTri <= max,
                        "Giá trị phải từ " + min + " đến " + max + ".");
                return giaTri;
            } catch (NumberFormatException e) {
                out.println("Lỗi: nhập số nguyên, không có dấu chấm/phẩy (ví dụ 25000).");
            } catch (IllegalArgumentException e) {
                out.println("Lỗi: " + e.getMessage());
            }
        }
    }

    public int soNguyen(String thongBao, int min, int max) {
        return (int) soNguyenDai(thongBao, min, max);
    }

    public LocalDate ngay(String thongBao) {
        while (true) {
            try { return QuyTac.docNgay(dong(thongBao)); }
            catch (DateTimeParseException e) {
                out.println("Lỗi: ngày phải tồn tại và có dạng dd/MM/yyyy, ví dụ 30/09/2026.");
            }
        }
    }

    public boolean xacNhan(String thongBao) {
        while (true) {
            String traLoi = dong(thongBao + " (y/n): ");
            if (traLoi.equalsIgnoreCase("y")) return true;
            if (traLoi.equalsIgnoreCase("n")) return false;
            out.println("Vui lòng nhập y hoặc n.");
        }
    }
}

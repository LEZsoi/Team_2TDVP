import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;


public final class HoaDon {
    private final String ma;
    private final LocalDateTime ngay;
    private final String maKhachHang; // Chuỗi rỗng = khách vãng lai.
    private final String tenKhachHang;
    private final String maNhanVien;
    private final String tenNhanVien;
    private final List<ChiTietHoaDon> chiTiet;
    private final long tienKhachDua;
    private boolean daHuy;

    public HoaDon(String ma, LocalDateTime ngay, String maKhach, String tenKhach,
                  String maNhanVien, String tenNhanVien, List<ChiTietHoaDon> chiTiet,
                  long tienKhachDua, boolean daHuy) {
        this.ma = QuyTac.chuanHoaMa(ma);
        this.ngay = Objects.requireNonNull(ngay, "Thiếu ngày lập hóa đơn.");
        Objects.requireNonNull(maKhach, "Thiếu mã khách hàng.");
        this.maKhachHang = maKhach.isBlank() ? "" : QuyTac.chuanHoaMa(maKhach);
        this.tenKhachHang = QuyTac.chuanHoaChuoi(tenKhach);
        this.maNhanVien = QuyTac.chuanHoaMa(maNhanVien);
        this.tenNhanVien = QuyTac.chuanHoaChuoi(tenNhanVien);
        Objects.requireNonNull(chiTiet, "Thiếu chi tiết hóa đơn.");
        QuyTac.yeuCau(!chiTiet.isEmpty(), "Hóa đơn phải có ít nhất một mặt hàng.");
        Set<String> cacMa = new HashSet<>();
        for (ChiTietHoaDon dong : chiTiet) {
            Objects.requireNonNull(dong, "Chi tiết không được null.");
            QuyTac.yeuCau(cacMa.add(dong.getMaSanPham()), "Một mã sản phẩm bị lặp trong hóa đơn.");
        }
        this.chiTiet = Collections.unmodifiableList(new ArrayList<>(chiTiet));
        QuyTac.yeuCau(tienKhachDua >= tongTien(), "Tiền khách đưa chưa đủ.");
        this.tienKhachDua = tienKhachDua;
        this.daHuy = daHuy;
    }

    public String getMa() { return ma; }
    public LocalDateTime getNgay() { return ngay; }
    public String getMaKhachHang() { return maKhachHang; }
    public String getTenKhachHang() { return tenKhachHang; }
    public String getMaNhanVien() { return maNhanVien; }
    public String getTenNhanVien() { return tenNhanVien; }
    public List<ChiTietHoaDon> getChiTiet() { return chiTiet; }
    public long getTienKhachDua() { return tienKhachDua; }
    public boolean isDaHuy() { return daHuy; }
    public String getTrangThai() { return daHuy ? "ĐÃ HỦY" : "ĐÃ THANH TOÁN"; }

    public long tongTien() {
        long tong = 0;
        for (ChiTietHoaDon dong : chiTiet) tong = Math.addExact(tong, dong.thanhTien());
        return tong;
    }

    public long tienThua() { return tienKhachDua - tongTien(); }

    // Chỉ CuaHang gọi sau khi đã kiểm tra và hoàn kho thành công.
    void danhDauHuy() {
        QuyTac.yeuCau(!daHuy, "Hóa đơn đã hủy trước đó.");
        daHuy = true;
    }
}

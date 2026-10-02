import java.util.Objects;


public final class ChiTietHoaDon {
    private final String maSanPham;
    private final String tenSanPham;
    private final int soLuong;
    private final long donGia;

    public ChiTietHoaDon(SanPham sanPham, int soLuong) {
        this(Objects.requireNonNull(sanPham).getMa(), sanPham.getTen(),
                soLuong, sanPham.giaBanThucTe());
    }

    /** Dùng khi đọc lại snapshot từ file; tuyệt đối không lấy giá hiện tại. */
    public ChiTietHoaDon(String ma, String ten, int soLuong, long donGia) {
        this.maSanPham = QuyTac.chuanHoaMa(ma);
        this.tenSanPham = QuyTac.chuanHoaChuoi(ten);
        QuyTac.yeuCau(soLuong > 0 && soLuong <= QuyTac.TON_KHO_TOI_DA,
                "Số lượng mua phải từ 1 đến 1.000.000.");
        QuyTac.yeuCau(donGia >= 0 && donGia <= QuyTac.GIA_TOI_DA, "Đơn giá snapshot không hợp lệ.");
        this.soLuong = soLuong;
        this.donGia = donGia;
    }

    public String getMaSanPham() { return maSanPham; }
    public String getTenSanPham() { return tenSanPham; }
    public int getSoLuong() { return soLuong; }
    public long getDonGia() { return donGia; }
    public long thanhTien() { return Math.multiplyExact(donGia, soLuong); }
}

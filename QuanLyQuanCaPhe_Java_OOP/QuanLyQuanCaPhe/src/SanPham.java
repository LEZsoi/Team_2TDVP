import java.time.LocalDate;

public abstract class SanPham implements KhuyenMai {
    private final String ma;
    private final String ten;
    private final long giaGoc;
    private int tonKho;
    private final int phanTramGiamGia;

    protected SanPham(String ma, String ten, long giaGoc, int tonKho, int giamGia) {
        this.ma = QuyTac.chuanHoaMa(ma);
        this.ten = QuyTac.chuanHoaChuoi(ten);
        QuyTac.kiemTraGia(giaGoc);
        QuyTac.kiemTraTonKho(tonKho);
        QuyTac.kiemTraGiamGia(giamGia);
        this.giaGoc = giaGoc;
        this.tonKho = tonKho;
        this.phanTramGiamGia = giamGia;
    }

    public String getMa() { return ma; }
    public String getTen() { return ten; }
    public long getGiaGoc() { return giaGoc; }
    public int getTonKho() { return tonKho; }
    public int getPhanTramGiamGia() { return phanTramGiamGia; }

    @Override
    public long giaBanThucTe() {
        // Chia cho 100, làm tròn nửa lên đến 1 đồng; giá gốc đã có giới hạn.
        return (giaGoc * (100 - phanTramGiamGia) + 50) / 100;
    }

    void nhapKho(int soLuong) {
        QuyTac.yeuCau(soLuong > 0, "Số lượng nhập phải lớn hơn 0.");
        long tonMoi = (long) tonKho + soLuong;
        QuyTac.yeuCau(tonMoi <= QuyTac.TON_KHO_TOI_DA, "Tồn kho vượt giới hạn.");
        tonKho = (int) tonMoi;
    }

    void xuatKho(int soLuong) {
        QuyTac.yeuCau(soLuong > 0 && soLuong <= tonKho, "Số lượng xuất không hợp lệ.");
        tonKho -= soLuong;
    }

    public abstract String getLoai();
    public abstract LocalDate getHanSuDung();
    public abstract boolean duocBan(LocalDate ngay);

    public boolean duocBan() { return duocBan(LocalDate.now()); }

    public String trangThai(LocalDate ngay) {
        if (!duocBan(ngay)) return "HẾT HẠN";
        return tonKho == 0 ? "HẾT HÀNG" : "CÒN BÁN";
    }
}

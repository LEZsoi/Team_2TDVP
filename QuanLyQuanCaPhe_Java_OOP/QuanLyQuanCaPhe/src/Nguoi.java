/** Thành viên 3 - Thủy Tiên: thông tin chung, mã định danh không thay đổi. */
public abstract class Nguoi {
    private final String ma;
    private String ten;
    private String soDienThoai;

    protected Nguoi(String ma, String ten, String soDienThoai) {
        this.ma = QuyTac.chuanHoaMa(ma);
        capNhat(ten, soDienThoai);
    }

    public String getMa() { return ma; }
    public String getTen() { return ten; }
    public String getSoDienThoai() { return soDienThoai; }
    public abstract String getVaiTro();

    public void capNhat(String ten, String soDienThoai) {
        // Kiểm tra cả hai trường trước khi gán để không cập nhật dở dang.
        String tenHopLe = QuyTac.chuanHoaChuoi(ten);
        String soHopLe = QuyTac.kiemTraSoDienThoai(soDienThoai);
        this.ten = tenHopLe;
        this.soDienThoai = soHopLe;
    }
}

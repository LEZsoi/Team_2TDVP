
public final class NhanVien extends Nguoi {
    public NhanVien(String ma, String ten, String soDienThoai) {
        super(ma, ten, soDienThoai);
    }
    @Override public String getVaiTro() { return "Nhân viên"; }
}

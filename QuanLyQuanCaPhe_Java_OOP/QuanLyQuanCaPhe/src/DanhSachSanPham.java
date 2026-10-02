import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;


public final class DanhSachSanPham {
    private final ArrayList<SanPham> danhSach = new ArrayList<>();

    public void them(SanPham sanPham) {
        Objects.requireNonNull(sanPham, "Thiếu sản phẩm.");
        QuyTac.yeuCau(timTheoMa(sanPham.getMa()) == null, "Mã sản phẩm đã tồn tại.");
        danhSach.add(sanPham);
    }

    public SanPham timTheoMa(String ma) {
        String maChuan = QuyTac.chuanHoaMa(ma);
        for (SanPham sanPham : danhSach) {
            if (sanPham.getMa().equals(maChuan)) return sanPham;
        }
        return null;
    }

    public List<SanPham> timTheoTen(String tuKhoa) {
        String khoa = QuyTac.khoaTimKiem(tuKhoa);
        ArrayList<SanPham> ketQua = new ArrayList<>();
        for (SanPham sanPham : danhSach) {
            if (QuyTac.khoaTimKiem(sanPham.getTen()).contains(khoa)) ketQua.add(sanPham);
        }
        return Collections.unmodifiableList(ketQua);
    }

    public void sua(SanPham sanPhamMoi) {
        Objects.requireNonNull(sanPhamMoi, "Thiếu sản phẩm.");
        SanPham cu = timTheoMa(sanPhamMoi.getMa());
        QuyTac.yeuCau(cu != null, "Không tìm thấy sản phẩm.");
        QuyTac.yeuCau(cu.getClass() == sanPhamMoi.getClass(), "Không đổi loại sản phẩm khi sửa.");
        QuyTac.yeuCau(cu.getTonKho() == sanPhamMoi.getTonKho(),
                "Không sửa trực tiếp tồn kho; hãy dùng nghiệp vụ nhập hàng/bán hàng.");
        danhSach.set(danhSach.indexOf(cu), sanPhamMoi);
    }

    // CuaHang phải kiểm tra lịch sử hóa đơn trước khi gọi phương thức này.
    void xoa(String ma) {
        SanPham sanPham = timTheoMa(ma);
        QuyTac.yeuCau(sanPham != null, "Không tìm thấy sản phẩm.");
        danhSach.remove(sanPham);
    }

    public List<SanPham> tatCa() {
        return Collections.unmodifiableList(new ArrayList<>(danhSach));
    }

    public List<SanPham> locTheoLoai(String loai) {
        QuyTac.yeuCau("THUC_PHAM".equals(loai) || "DO_UONG".equals(loai), "Loại không hợp lệ.");
        ArrayList<SanPham> ketQua = new ArrayList<>();
        for (SanPham sanPham : danhSach) if (sanPham.getLoai().equals(loai)) ketQua.add(sanPham);
        return Collections.unmodifiableList(ketQua);
    }

    /** 1: còn bán; 2: hết hạn; 3: hết hàng (có thể đồng thời hết hạn). */
    public List<SanPham> locTheoTrangThai(int trangThai, LocalDate ngay) {
        QuyTac.yeuCau(trangThai >= 1 && trangThai <= 3, "Trạng thái lọc không hợp lệ.");
        Objects.requireNonNull(ngay);
        ArrayList<SanPham> ketQua = new ArrayList<>();
        for (SanPham sanPham : danhSach) {
            if ((trangThai == 1 && sanPham.duocBan(ngay) && sanPham.getTonKho() > 0)
                    || (trangThai == 2 && !sanPham.duocBan(ngay))
                    || (trangThai == 3 && sanPham.getTonKho() == 0)) ketQua.add(sanPham);
        }
        return Collections.unmodifiableList(ketQua);
    }
}

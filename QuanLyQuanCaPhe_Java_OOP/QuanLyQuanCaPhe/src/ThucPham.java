import java.time.LocalDate;
import java.util.Objects;


public final class ThucPham extends SanPham {
    private final LocalDate hanSuDung;

    public ThucPham(String ma, String ten, long gia, int ton, int giam, LocalDate han) {
        super(ma, ten, gia, ton, giam);
        this.hanSuDung = Objects.requireNonNull(han, "Thiếu hạn sử dụng.");
    }

    @Override public String getLoai() { return "THUC_PHAM"; }
    @Override public LocalDate getHanSuDung() { return hanSuDung; }
    @Override public boolean duocBan(LocalDate ngay) {
        return !hanSuDung.isBefore(Objects.requireNonNull(ngay));
    }
}

import java.time.LocalDate;
import java.util.Objects;

/** Thành viên 2 - Tấn Viên: đồ uống cũng kiểm tra hạn sử dụng theo đề bài. */
public final class DoUong extends SanPham {
    private final LocalDate hanSuDung;

    public DoUong(String ma, String ten, long gia, int ton, int giam, LocalDate han) {
        super(ma, ten, gia, ton, giam);
        this.hanSuDung = Objects.requireNonNull(han, "Thiếu hạn sử dụng.");
    }

    @Override public String getLoai() { return "DO_UONG"; }
    @Override public LocalDate getHanSuDung() { return hanSuDung; }
    @Override public boolean duocBan(LocalDate ngay) {
        return !hanSuDung.isBefore(Objects.requireNonNull(ngay));
    }
}

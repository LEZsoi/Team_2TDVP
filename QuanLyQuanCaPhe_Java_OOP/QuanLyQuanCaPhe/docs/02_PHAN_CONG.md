# Phân công và mô tả công việc

## Phân công giữ nguyên theo tài liệu

Đây là mô tả trách nhiệm của từng người đối với **bản mã nguồn được bàn giao**, không phải xác nhận rằng các thành viên đã tự hoàn thành hay đã kiểm thử công việc. Nhóm nên dùng tài liệu này để chia phần học, rà soát và thuyết trình.

| Thành viên | Mảng phụ trách | Tệp chính | Số tệp |
|---|---|---|---:|
| Thanh Dương | Nền tảng và tiện ích | QuyTac.java, NhapLieu.java | 2 |
| Tấn Viên | Sản phẩm và thực đơn | SanPham.java, ThucPham.java, DoUong.java, DanhSachSanPham.java | 4 |
| Thủy Tiên | Mô hình khách hàng và nhân viên | Nguoi.java, KhachHang.java, NhanVien.java | 3 |
| Đức Phúc | Khuyến mãi, hóa đơn và nghiệp vụ cửa hàng | KhuyenMai.java, ChiTietHoaDon.java, HoaDon.java, CuaHang.java | 4 |
| Đình Triệu | Lưu trữ, menu và kiểm thử | LuuTru.java, Main.java, KiemThu.java | 3 |

Tổng là **16 tệp**, gồm 15 lớp và 1 interface. Số tệp không phản ánh hoàn toàn độ khó: phần Đức Phúc có nhiều quy tắc cần phối hợp; phần Đình Triệu có trách nhiệm tích hợp và kiểm thử toàn chương trình.

## Thành viên 1 Thanh Dương

**Mục tiêu:** mọi phần của chương trình cùng dùng một quy tắc kiểm tra và nhập liệu, tránh chỗ này chấp nhận dữ liệu mà chỗ khác từ chối.

**QuyTac.java.** Phụ trách tên cửa hàng, giới hạn giá, tồn kho và độ dài tên. Giải thích được `static final`, constructor private, `yeuCau`, `chuanHoaMa`, `chuanHoaChuoi`, `kiemTraSoDienThoai`, `kiemTraGia`, `kiemTraTonKho`, `kiemTraGiamGia`, `docNgay`, `dinhDangTien`, `khoaTimKiem`. Tìm kiếm bỏ dấu là tiện ích để người dùng gõ `ca phe` vẫn tìm được “Cà phê”.

**NhapLieu.java.** Phụ trách đọc số, chuỗi, mã, điện thoại, ngày và xác nhận. Dùng `nextLine()` rồi chuyển kiểu, không trộn `nextInt()` với `nextLine()`. Bắt sai định dạng số và ngày để yêu cầu nhập lại; khi hết luồng nhập phải thoát vòng lặp bằng ngoại lệ để `Main` kết thúc an toàn.

**Bàn giao cho nhóm:** chữ ký hàm và thông báo lỗi ổn định; toàn bộ `Main` dùng lớp này thay vì tự tạo nhiều Scanner. Các lớp mô hình cũng gọi `QuyTac` để không thể bỏ qua kiểm tra khi gọi hàm ngoài menu.

**Tiêu chí hoàn thành:** nhập `abc` ở ô số không làm chương trình văng; nhập giá âm bị chặn; `31/02/2030` không được chấp nhận; mã ` du01 ` trở thành `DU01`; SĐT sai bị yêu cầu nhập lại. Đối chiếu ca 01–04, 12, 36–37 trong KiemThu.

**Khi trình bày:** giải thích sự khác nhau giữa kiểm tra dữ liệu ở giao diện và kiểm tra ở mô hình; vì sao cần cả hai; vì sao ngày phải kiểm tra nghiêm ngặt và tiền dùng số nguyên đồng.

## Thành viên 2 Tấn Viên

**Mục tiêu:** biểu diễn hàng hóa bằng kế thừa, quản lý thực đơn và cung cấp sản phẩm hợp lệ cho nghiệp vụ bán hàng.

**SanPham.java.** Phụ trách lớp abstract với mã, tên, giá gốc, tồn kho và phần trăm giảm. Tất cả thuộc tính private; mã không đổi. `giaBanThucTe()` hiện thực interface KhuyenMai do Đức Phúc phụ trách. Giải thích phép làm tròn và vì sao tồn kho chỉ thay đổi qua hàm nhập/xuất có kiểm tra.

**ThucPham.java và DoUong.java.** Cả hai kế thừa SanPham, gọi `super(...)`, bổ sung HSD và ghi đè `getLoai`, `getHanSuDung`, `duocBan(LocalDate)`. Theo tài liệu, cả đồ uống cũng có hạn dùng. Hai lớp có logic kiểm tra ngày giống nhau; việc tách lớp thể hiện hai loại sản phẩm, không tự thêm thuộc tính ngoài đề như size, topping hoặc công thức pha chế.

**DanhSachSanPham.java.** Phụ trách `ArrayList<SanPham>`, thêm không trùng mã, sửa giữ loại và tồn kho, tìm mã/tên, lọc theo loại và trạng thái. Trả bản sao danh sách chỉ đọc để bên ngoài không tự xóa/thêm phần tử. Hàm xóa nội bộ được CuaHang gọi sau kiểm tra lịch sử hóa đơn.

**Bàn giao cho nhóm:** phương thức lấy mã/tên/giá/tồn/HSD và danh sách sản phẩm; giá sau giảm phải là giá dùng trong ChiTietHoaDon. Giữ nguyên tên kiểu `THUC_PHAM`, `DO_UONG` vì LuuTru dùng chúng để phục hồi đúng lớp con.

**Tiêu chí hoàn thành:** CRUD thực đơn hoạt động; trùng mã bị chặn; lọc đúng loại; ngày HSD còn bán, ngày sau không bán; hết hàng không hiện trong bộ lọc còn bán. Đối chiếu ca 05–11, 14, 17, 22–23.

**Khi trình bày:** tạo biến kiểu SanPham giữ đối tượng DoUong hoặc ThucPham; gọi phương thức ghi đè để chứng minh đa hình. Giải thích vì sao không thể `new SanPham(...)` và vì sao tìm kiếm trả danh sách nhưng tìm mã trả một đối tượng hoặc null.

## Thành viên 3 Thủy Tiên

**Mục tiêu:** biểu diễn khách hàng và nhân viên từ một lớp người chung, bảo đảm thông tin định danh hợp lệ.

**Nguoi.java.** Phụ trách lớp abstract với mã, tên, SĐT; kiểm tra điện thoại bằng quy tắc `0[0-9]{9}`. Hàm `capNhat` kiểm tra tên và SĐT trước khi gán cả hai, tránh trường hợp tên đổi rồi nhưng SĐT bị lỗi. Mã là final và không sửa.

**KhachHang.java và NhanVien.java.** Phụ trách constructor gọi super và `getVaiTro()` tương ứng. Các lớp con tối giản vì đề chưa yêu cầu lương, chức vụ, điểm thưởng hoặc cấp thành viên. Không thêm các tính năng đó vào phần bắt buộc.

**Phối hợp với Đức Phúc:** Thủy Tiên bàn giao các lớp mô hình; `CuaHang` sở hữu ArrayList khách/nhân viên và các hàm thêm/sửa/xóa/tìm theo mô tả gốc. Nhóm rà soát cùng nhau việc chặn xóa người đã có hóa đơn và giữ snapshot tên cũ.

**Phối hợp với Đình Triệu:** thống nhất menu 3 và 4 dùng cùng cách nhập mã, tên, điện thoại; file phải ghi lại SĐT dưới dạng chuỗi để giữ số 0 đầu.

**Tiêu chí hoàn thành:** thêm hai người trùng mã trong cùng loại bị chặn; SĐT sai không được lưu; sửa thông tin người không làm đổi tên trên hóa đơn cũ; xóa người chưa có giao dịch được. Đối chiếu ca 12–13, 18, 24, 29–30.

**Khi trình bày:** giải thích `Nguoi` dùng để dùng chung dữ liệu/hành vi, còn `KhachHang` và `NhanVien` phân biệt vai trò. Không nhầm “vai trò mô hình” với đăng nhập/phân quyền, vì chương trình chưa có chức năng đăng nhập.

## Thành viên 4 Đức Phúc

**Mục tiêu:** bảo đảm tiền, tồn kho và lịch sử nhất quán qua toàn bộ quá trình bán hoặc hủy. Đây là phần nghiệp vụ cốt lõi của đề.

**KhuyenMai.java.** Phụ trách interface khai báo `long giaBanThucTe()`. Thống nhất sớm với Tấn Viên rằng giá là số nguyên đồng, giảm giá được áp dụng ở đơn vị sản phẩm. Interface chỉ có một tệp dù tài liệu nhắc lại hai chỗ.

**ChiTietHoaDon.java.** Phụ trách snapshot của mã, tên, số lượng, đơn giá. Tất cả trường final; không giữ đối tượng SanPham và không có setter. Constructor nhận SanPham dùng khi bán, constructor nhận các giá trị dùng khi đọc file. `thanhTien()` nhân đơn giá với số lượng bằng long.

**HoaDon.java.** Phụ trách mã, ngày giờ, thông tin khách/nhân viên, danh sách chi tiết, tiền khách đưa và trạng thái hủy. Copy danh sách đầu vào và trả danh sách chỉ đọc. Tính `tongTien`, `tienThua` và tên trạng thái. Không tự hoàn kho trong lớp này: CuaHang mới biết kho sản phẩm để điều phối.

**CuaHang.java.** Phụ trách sở hữu danh sách sản phẩm, khách, nhân viên, hóa đơn; CRUD người; nhập kho; kiểm tra giỏ; thanh toán; tạo mã hóa đơn; hủy và báo cáo. Khi bán, kiểm tra người và tất cả dòng hàng, tạo hóa đơn hợp lệ rồi mới trừ kho. Khi hủy, kiểm tra mọi dòng trước rồi mới hoàn tất cả, đánh dấu hủy sau cùng. Doanh thu luôn tính từ hóa đơn còn hiệu lực.

**Bàn giao cho Đình Triệu:** chữ ký các hàm `kiemTraGioHang`, `tinhTienGioHang`, `banHang`, `huyHoaDon`, `doanhThu`, `tongDoanhThu`, `soLuongDaBan`, cùng getter cho lưu trữ. `napHoaDon` chỉ dùng khi đọc file và không tác động kho lần nữa.

**Tiêu chí hoàn thành:** thiếu tiền, hết hạn hoặc thiếu kho không làm thay đổi dữ liệu; sửa giá không đổi hóa đơn cũ; hủy đúng một lần; tiền hoàn là tiền hàng; doanh thu bỏ hóa đơn hủy; lịch sử không bị xóa gián tiếp. Đối chiếu ca 15–35, 38, 43.

**Khi trình bày:** trình bày ví dụ DU01 × 2 + TP01 × 1 trong dữ liệu mẫu = 70.000đ, khách đưa 100.000đ, thừa 30.000đ. Hủy trả 70.000đ và hoàn đúng 2 DU01, 1 TP01. Chỉ rõ hai vòng lặp “kiểm tra trước, cập nhật sau” trong hàm hủy và giải thích giới hạn một tiến trình.

## Thành viên 5 Đình Triệu

**Mục tiêu:** biến các lớp nghiệp vụ thành chương trình dùng được, lưu bền dữ liệu và có bằng chứng kiểm thử.

**LuuTru.java.** Phụ trách định dạng Properties text UTF-8, phiên bản file, số bản ghi và khóa từng trường. Khi đọc, phục hồi đúng lớp DoUong/ThucPham và snapshot, không gọi lại banHang. Ghi file tạm, sao lưu file cũ, đổi tên để giảm rủi ro ghi hỏng. Sai trường, số, ngày, trạng thái hoặc tham chiếu phải báo IOException có thông tin.

**Main.java.** Phụ trách vòng lặp menu, hiển thị, giỏ LinkedHashMap, xác nhận thanh toán/xóa/hủy/nạp lại. Gọi NhapLieu để nhập và CuaHang để xử lý; không viết lại công thức giảm giá hoặc tự chỉnh kho trong menu. Tự lưu sau thay đổi; có lỗi ghi phải hiển thị trạng thái chưa lưu.

**KiemThu.java.** Phụ trách các ca kiểm thử độc lập, kiểm tra giá trị tiền/kho sau thành công và sau thất bại. Dùng Clock cố định để kiểm tra biên ngày, thư mục tạm cho file và luồng nhập giả lập cho NhapLieu. Không cần JUnit. Có mã thoát khác 0 khi một ca thất bại.

**Bàn giao cho nhóm:** mã chạy, script, log kiểm thử, dữ liệu mẫu tự tạo, hướng dẫn mở chương trình, hướng dẫn phục hồi và kịch bản trình diễn. Kết quả 43/43 là kết quả đã chạy trong môi trường bàn giao; cần chạy lại trên máy thực tế trước buổi bảo vệ.

**Tiêu chí hoàn thành:** đóng/mở lại không mất kho, hóa đơn hay trạng thái hủy; đọc file lỗi không đè dữ liệu; nhập chữ ở ô số không văng chương trình; tất cả ca test đạt. Đối chiếu ca 36–43 và log kiểm thử console.

**Khi trình bày:** giải thích vì sao text vẫn lưu được tiếng Việt, vì sao không dùng ObjectOutputStream, vì sao không cộng/trừ kho lại lúc đọc file, và phân biệt lỗi nhập liệu, lỗi nghiệp vụ, lỗi tệp.

## Trình tự phối hợp đề xuất

| Bước | Người phối hợp | Sản phẩm cần thống nhất trước bước sau |
|---|---|---|
| 1 | Thanh Dương và Đức Phúc | QuyTac, kiểu tiền long, chữ ký KhuyenMai |
| 2 | Tấn Viên và Thủy Tiên | Mô hình sản phẩm/người, constructor/getter ổn định |
| 3 | Đức Phúc với Tấn Viên | Snapshot và logic kiểm tra/trừ/hoàn kho |
| 4 | Đức Phúc với Thủy Tiên | CRUD người và tham chiếu từ hóa đơn |
| 5 | Đình Triệu với cả nhóm | Menu, lưu/đọc, tích hợp tất cả nghiệp vụ |
| 6 | Cả 5 thành viên | Chạy test, demo, giải thích được luồng đi qua phần mình và phần liên quan |

Khi sửa một hàm dùng chung, báo cho người gọi hàm đó và chạy lại toàn bộ KiemThu. Trước khi ghép bài, thống nhất mã hóa UTF-8, JDK 17 và default package. Mỗi người nên biết luồng `Main → CuaHang → mô hình → LuuTru`, không chỉ học thuộc tệp mình được phân công.

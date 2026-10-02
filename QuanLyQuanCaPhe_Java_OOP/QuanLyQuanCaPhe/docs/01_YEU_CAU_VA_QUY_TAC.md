# Đối chiếu yêu cầu và quy tắc nghiệp vụ

## Căn cứ thực hiện

Nguồn là tài liệu Word người dùng cung cấp, tên đồ án **Quản lý quán cà phê**, triển khai Java hướng đối tượng trên console. Chương trình không chuyển sang đề tài quán net hoặc cửa hàng tiện lợi trong các trao đổi khác.

Tài liệu ghi 17 class nhưng chỉ liệt kê 16 tên tệp khác nhau, trong đó `KhuyenMai` là interface. `KhuyenMai` xuất hiện ở cả mục 3 và mục 10, nhưng chỉ cần một tệp. Có 15 khai báo `class` và 1 khai báo `interface`; 2 lớp trừu tượng là `SanPham`, `Nguoi`.

## Bảng đối chiếu

| Nội dung trong tài liệu | Tệp thực hiện | Kết quả có thể kiểm tra |
|---|---|---|
| Hằng số, chuẩn hóa, kiểm tra và định dạng tiền | QuyTac.java | Chặn mã sai, chuẩn hóa hoa/thường, định dạng VND |
| Nhập bàn phím an toàn, bắt sai kiểu | NhapLieu.java | Nhập chữ khi cần số sẽ yêu cầu nhập lại |
| Interface giaBanThucTe | KhuyenMai.java | SanPham implements KhuyenMai, dùng long đồng |
| Lớp sản phẩm trừu tượng, đóng gói 5 thuộc tính chung | SanPham.java | Thuộc tính private; lớp con gọi super |
| Hai lớp con có HSD và duocBan theo ngày | ThucPham.java, DoUong.java | Hết hạn bị chặn; đúng ngày HSD còn bán |
| Danh sách ArrayList, thêm/sửa/xóa/tìm/lọc | DanhSachSanPham.java | Menu 1; xóa đi qua CuaHang để kiểm tra lịch sử |
| Người trừu tượng và regex điện thoại | Nguoi.java | Đúng 10 chữ số, bắt đầu 0 |
| Phân biệt khách và nhân viên | KhachHang.java, NhanVien.java | Kế thừa Nguoi, ghi đè getVaiTro |
| Snapshot dòng hóa đơn | ChiTietHoaDon.java | Giá/tên cũ giữ nguyên khi sửa sản phẩm |
| Mã, ngày, tên người, dòng hàng, trạng thái, tổng tiền | HoaDon.java | Menu 6; ĐÃ THANH TOÁN hoặc ĐÃ HỦY |
| Kho, nhân sự, giỏ, bán, tiền thừa, hủy, doanh thu | CuaHang.java | Menu 2–7 và KiemThu |
| Đọc/ghi toàn bộ cửa hàng thành text | LuuTru.java | UTF-8 Properties, đọc lại vẫn đúng hóa đơn/kho |
| Điểm vào và menu console | Main.java | Main.main; các menu kiểm tra dữ liệu đầu vào |
| Kiểm tra logic và chống ngoại lệ | KiemThu.java | 43 ca, 177 phép kiểm tra |

## Quy tắc triển khai được làm rõ

Những quy tắc dưới đây cụ thể hóa các chỗ tài liệu chưa nói chi tiết; không coi chúng là nội dung được trích nguyên văn từ đề.

**Mã định danh.** Mã dài 1–20 ký tự, gồm A–Z, 0–9, dấu gạch dưới hoặc gạch ngang; ký tự đầu là chữ/số. Chuẩn hóa sang in hoa và bỏ khoảng trắng hai đầu. Mã duy nhất trong từng danh sách; không đổi mã khi sửa. Ví dụ ` du01 ` và `DU01` là cùng một mã.

**Tên và điện thoại.** Tên không rỗng, tối đa 120 ký tự sau chuẩn hóa, khoảng trắng liên tiếp được gộp. Số điện thoại đúng regex `0[0-9]{9}`; đây là kiểm tra định dạng theo đề, không xác minh số đó có tồn tại trên mạng viễn thông.

**Tiền và khuyến mãi.** Giá gốc từ 1 đến 10.000.000 đồng, giảm giá nguyên từ 0 đến 100%. Giá bán = giá gốc × (100 − phần trăm giảm) / 100, làm tròn nửa lên đến 1 đồng. Giá 101đ giảm 50% thành 51đ. Lưu tiền bằng `long`; không dùng `double` để tránh sai số số thực. Chiết khấu áp dụng vào từng đơn vị trước khi nhân số lượng. Không có VAT, phí phục vụ, tích điểm hoặc chiết khấu toàn hóa đơn trong phạm vi đề.

**Kho.** Tồn kho 0–1.000.000 đơn vị, nhập thêm phải dương và không vượt trần. Không cho sửa trực tiếp tồn khi sửa sản phẩm. Cập nhật kho đi qua nhập hàng, bán hàng và hủy. Đề không có giá vốn và nhật ký phiếu nhập nên chương trình chỉ lưu tồn hiện tại; doanh thu không phải lợi nhuận.

**Hạn sử dụng.** Được bán trong ngày HSD, bị coi hết hạn từ ngày kế tiếp. Ngày dùng là ngày trên máy chạy chương trình. `duocBan(ngay)` xét hạn; kiểm tra số lượng tồn thực hiện riêng. Cho phép lưu hàng đã hết hạn để xem/tìm/lọc nhưng không cho bán. Mỗi mã chỉ chứa một HSD; lô mới khác hạn phải dùng mã mới. Sửa HSD chỉ nhằm hiệu chỉnh dữ liệu nhập sai, không được coi là cách làm mới thực phẩm hết hạn.

**Giỏ hàng.** `Main` dùng `LinkedHashMap<String,Integer>` để gộp số lượng theo mã, giữ thứ tự thêm món. Không phát sinh hóa đơn nháp, không giữ chỗ và không trừ kho khi thêm giỏ. Khi thanh toán, `CuaHang` kiểm tra lại toàn bộ giỏ theo cùng một thời điểm.

**Thanh toán.** Bắt buộc nhân viên đang tồn tại. Mã khách có thể để trống để dùng tên “Khách vãng lai”; nếu nhập mã thì phải tồn tại. Tổng tiền phải không lớn hơn số tiền khách đưa. Chỉ sau khi tất cả điều kiện đều đạt mới trừ kho và lưu hóa đơn. Tiền thừa = tiền khách đưa − tổng tiền hàng.

**Snapshot.** Chi tiết giữ bản chụp mã, tên, số lượng và đơn giá sau giảm. Hóa đơn giữ mã/tên khách, mã/tên nhân viên, ngày giờ, tiền khách đưa. Chi tiết bất biến; tổng hóa đơn tính từ snapshot. Không tham chiếu giá menu hiện tại để tính hóa đơn lịch sử.

**Hủy.** Chỉ hủy toàn bộ hóa đơn, tối đa một lần. Trước khi hoàn kho, kiểm tra tất cả sản phẩm có tồn tại và không vượt trần. Hóa đơn vẫn lưu trong lịch sử với tổng tiền gốc, đổi trạng thái ĐÃ HỦY và bị loại khỏi doanh thu. Số tiền cần hoàn khách là tiền hàng; tiền thừa đã trả lúc bán không được trả lại lần nữa. Đây là mô phỏng nghiệp vụ console, không kết nối cổng thanh toán.

**Doanh thu.** Là tổng giá trị hóa đơn chưa hủy. Lọc khoảng ngày bao gồm cả hai ngày biên và theo ngày lập hóa đơn. Nếu hủy vào ngày sau, hóa đơn bị loại khỏi doanh thu của kỳ lập ban đầu; chương trình không có sổ dòng tiền hoàn trả theo ngày hủy. Số lượng bán ròng cũng bỏ hóa đơn hủy.

**Xóa dữ liệu.** Không cho xóa sản phẩm, khách hoặc nhân viên đã xuất hiện trong lịch sử hóa đơn, kể cả hóa đơn hủy. Quy tắc này bảo toàn tham chiếu và khả năng hoàn kho. Đối tượng chưa có giao dịch vẫn xóa được. Đây là lựa chọn đơn giản thay cho cơ chế xóa mềm mà đề chưa yêu cầu.

**Lưu và phục hồi.** Lưu sản phẩm, người và hóa đơn vào một file text. Giỏ tạm chưa thanh toán không lưu. Sau mỗi thay đổi thành công tự lưu; có lỗi ghi thì giữ thay đổi trong bộ nhớ và báo chưa lưu. Khi đọc, tạo cửa hàng mới rồi mới thay trạng thái đang sử dụng. Không tự tạo dữ liệu mẫu đè lên file lỗi.

## Phạm vi phù hợp đồ án môn học

Chạy một tiến trình trên một máy; không có đăng nhập, phân quyền, giao diện đồ họa, SQL, nhiều chi nhánh, nhiều lô hàng trên cùng mã, thanh toán mạng, hoàn một phần hoặc báo cáo lợi nhuận. Các giới hạn này giúp nội dung tập trung vào OOP và đúng danh sách lớp được giao.

Giao dịch kiểm tra trước khi đổi dữ liệu bảo đảm các lỗi nghiệp vụ thông thường không gây thay đổi dở dang trong tiến trình đơn luồng. Nó không tương đương transaction của cơ sở dữ liệu khi chạy đồng thời nhiều tiến trình hoặc khi mất điện. Ghi file tạm, đổi tên và bản `.bak` giảm rủi ro mất dữ liệu nhưng không thay thế hệ quản trị cơ sở dữ liệu.

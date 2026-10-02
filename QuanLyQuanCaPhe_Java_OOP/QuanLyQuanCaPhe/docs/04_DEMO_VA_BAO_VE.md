# Kịch bản demo và gợi ý bảo vệ

## Chuẩn bị trước khi trình bày

Chạy `test.bat` trên Windows hoặc `sh test.sh` trên macOS/Linux. Dùng bộ dữ liệu riêng để các con số dưới đây không bị ảnh hưởng bởi lần chạy trước:

```sh
java -Dfile.encoding=UTF-8 -cp out Main --data data/demo_bao_ve.txt
```

Nếu `demo_bao_ve.txt` đã tồn tại, chọn một tên mới để chương trình tạo mẫu. Không tạo trước một file rỗng. Mở Terminal đủ rộng, phông có dấu tiếng Việt và để sẵn mã CuaHang, SanPham, ChiTietHoaDon.

## Demo khoảng 10 đến 15 phút

| Bước | Thao tác | Kết quả cần chỉ ra | Người phù hợp |
|---|---|---|---|
| 1 | Menu 1 → 1 | 5 sản phẩm, có hàng hết hạn/hết kho | Tấn Viên |
| 2 | Menu 1 → 6, tìm `ca phe`; menu 1 → 8 → 2 | Tìm không dấu; lọc được TP02 hết hạn | Tấn Viên |
| 3 | Menu 3 → 2, thêm KH03, thử SĐT sai rồi đúng | Sai định dạng không được lưu | Thủy Tiên và Thanh Dương |
| 4 | Menu 5, thử thêm TP02; sau đó thử DU03 | Hết hạn/hết kho đều bị chặn | Đức Phúc |
| 5 | Trong giỏ: thêm DU01 × 2 và TP01 × 1 | Tạm tính 70.000đ, chưa trừ kho | Đức Phúc |
| 6 | Chọn 5 thanh toán, NV01, KH01, tiền 100000, y | HD000001, thừa 30.000đ, kho 38/19 | Đức Phúc |
| 7 | Menu 1 → 3 sửa giá DU01 thành 35000, giảm 0, HSD hợp lệ | Giá hiện tại đổi; không sửa tồn | Tấn Viên |
| 8 | Menu 6 → 2, HD000001 | Giá DU01 trong hóa đơn vẫn 25.000đ | Đức Phúc |
| 9 | Thoát và mở lại đúng file `--data` | Hóa đơn và giá/kho hiện tại còn nguyên | Đình Triệu |
| 10 | Menu 6 → 3, HD000001, y; menu 7 → 1 | Hoàn kho, hoàn 70.000đ, doanh thu 0 | Đức Phúc |
| 11 | Hủy HD000001 lần nữa | Báo đã hủy; không hoàn kho hai lần | Đức Phúc |
| 12 | Chạy KiemThu | 43 ca đạt, giải thích một ca lỗi nghiệp vụ | Đình Triệu |

Kịch bản trên chỉ tạo một hóa đơn nên kỳ vọng doanh thu 0 sau hủy là chính xác. Nếu nhóm tự bán thêm, doanh thu phải bằng các hóa đơn khác chưa hủy, không luôn bằng 0.

## Chuỗi nhập cho giao dịch cơ bản

Mỗi dòng là một lần nhập và nhấn Enter, bắt đầu từ menu chính của bộ dữ liệu mới. Các dòng tên menu không cần nhập.

```text
5
1
DU01
2
1
TP01
1
5
NV01
KH01
100000
y
```

Sau giao dịch, chọn `6`, `2`, `HD000001` để xem chi tiết. Chọn `7`, `1` để xem tổng doanh thu. Chọn `0` để thoát từ menu chính.

## Tình huống phản biện thường gặp

**Tại sao chỉ có 16 tệp khi đề ghi 17 class?** Danh sách tên duy nhất trong đề có 15 lớp và 1 interface. KhuyenMai được nhắc hai lần nhưng không tạo hai tệp trùng tên. Nhóm giữ đúng phân công và nêu rõ chênh lệch để xác nhận với giảng viên nếu cần bổ sung.

**Tại sao dùng lớp trừu tượng SanPham?** Vì các loại sản phẩm có phần dữ liệu chung, nhưng cần xác định loại và quy tắc hạn dùng thông qua lớp cụ thể. Không có sản phẩm chung chung được khởi tạo trực tiếp.

**Interface KhuyenMai khác abstract SanPham ở đâu?** KhuyenMai quy định hợp đồng tính giá bán. SanPham giữ trạng thái chung và hiện thực công thức tính giá. Interface không lưu thuộc tính riêng của từng đối tượng sản phẩm như tồn kho.

**Đa hình nằm ở đâu?** ArrayList chứa SanPham nhưng đối tượng thực tế là DoUong/ThucPham. Khi gọi `getLoai` hoặc `duocBan`, Java dùng phương thức của lớp thực tế. Nguoi cũng có hai lớp con ghi đè `getVaiTro`.

**Tại sao không có setter cho mọi trường?** Không phải mọi trạng thái đều được phép thay đổi tự do. Mã định danh và snapshot phải giữ nguyên; tồn kho cần kiểm tra. Khi sửa sản phẩm, tạo đối tượng mới hợp lệ rồi thay trong danh sách, giữ mã/loại/tồn.

**Tại sao không dùng double cho tiền?** Tiền trong bài là đồng nguyên; double có sai số biểu diễn. Dùng long, giới hạn giá/số lượng và công thức làm tròn rõ ràng giúp kết quả nhất quán.

**Hết hạn đúng ngày có bán không?** Bản này quy ước được bán trong chính ngày HSD, từ ngày sau mới hết hạn. `duocBan` kiểm tra ngày, còn CuaHang kiểm tra đủ tồn riêng. Đây là quy tắc mô phỏng của đề án, không phải hướng dẫn bảo quản thực phẩm thực tế.

**Nếu khách không đưa đủ tiền thì sao?** HoaDon từ chối trước khi CuaHang trừ kho. Giỏ vẫn còn để sửa hoặc thanh toán lại. Không tạo hóa đơn lỗi và không làm nhảy mã hóa đơn.

**Nếu món thứ hai hết hàng thì món đầu đã bị trừ chưa?** Chưa. Toàn bộ giỏ được kiểm tra trước. Chỉ khi tất cả dòng và số tiền đều hợp lệ mới chạy vòng lặp xuất kho.

**Tại sao sửa giá không thay đổi hóa đơn cũ?** ChiTietHoaDon lưu giá trị mã/tên/số lượng/đơn giá tại lúc bán và không giữ SanPham. Tổng cũ được tính từ snapshot, không tính từ menu hiện tại.

**Nếu hủy hóa đơn có tiền khách đưa 100.000đ và tổng 70.000đ thì hoàn bao nhiêu?** Hoàn 70.000đ vì 30.000đ tiền thừa đã trả lúc thanh toán. Hủy còn hoàn số lượng vào kho và đánh dấu hủy để loại khỏi doanh thu.

**Tại sao không xóa sản phẩm đã bán?** Cần giữ tham chiếu và khả năng hủy/hoàn kho. Bản đơn giản chặn xóa; hướng mở rộng có thể dùng trạng thái ngừng kinh doanh thay vì xóa vật lý.

**Nếu nhân viên đổi tên thì hóa đơn cũ thế nào?** Hóa đơn giữ tên nhân viên snapshot tại lúc bán; tên hiện tại trong danh sách nhân viên có thể khác. Mã nhân viên không thay đổi.

**Tại sao đọc file không gọi lại banHang?** File đã lưu tồn hiện tại. Nếu gọi banHang lần nữa, tồn sẽ bị trừ thêm và mã/giờ hóa đơn cũng thay đổi. LuuTru dựng lại các snapshot rồi nạp lịch sử.

**Có tính lợi nhuận được không?** Chưa. Đề không có giá vốn hoặc chi phí, nên chỉ thống kê doanh thu. Không dùng doanh thu trừ tồn kho để gọi là lợi nhuận.

**Có chạy đồng thời hai máy trên một file không?** Chưa hỗ trợ. Mô hình một tiến trình console giữ dữ liệu trong RAM và ghi toàn bộ file; nhiều tiến trình có thể ghi đè trạng thái nhau. Nếu mở rộng cần lưu trữ có transaction và kiểm soát đồng thời.

**Kiểm thử đạt có nghĩa không còn lỗi không?** Không. Nó chứng minh các tình huống đã kiểm tra cho kết quả mong đợi. Nhóm vẫn cần chạy thử trên máy bảo vệ, rà soát và bổ sung ca khi đổi yêu cầu.

## Checklist trước buổi bảo vệ

- Chạy được cả bản nguồn và bản JAR; nếu đã sửa nguồn thì ưu tiên chạy bản biên dịch mới.
- Có một bộ dữ liệu demo mới và một bản sao của dữ liệu quan trọng.
- Không để nhiều chương trình cùng ghi một file.
- Mỗi thành viên chỉ được phương thức thuộc phần mình và giải thích đầu vào/đầu ra.
- Thực hiện được một ca thành công và một ca bị từ chối, kiểm tra tiền/kho sau cả hai.
- Nêu đúng phạm vi và chênh lệch số lớp; không tuyên bố có đăng nhập, nhiều lô, SQL hoặc phân quyền khi mã chưa làm.

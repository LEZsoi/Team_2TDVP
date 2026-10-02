# Đồ án Java OOP quản lý quán cà phê

Chương trình chạy trên **console**, xây dựng theo tài liệu **MÔ TẢ ĐỒ ÁN VÀ PHÂN CÔNG (dự kiến).docx** bạn cung cấp. Mục tiêu là quản lý thực đơn, kho, khách hàng, nhân viên, bán hàng, lịch sử hóa đơn, hủy hóa đơn và doanh thu bằng kiến thức hướng đối tượng Java.

Mã nguồn có chú thích tiếng Việt, không dùng thư viện ngoài, không cần mạng hoặc cơ sở dữ liệu để chạy. Dữ liệu được lưu trong file text UTF-8. Mỗi tệp Java ghi rõ người phụ trách theo phân công gốc.

**Chênh lệch trong đề:** tài liệu ghi “17 class” nhưng danh sách và phân công thực tế có **15 lớp + 1 interface `KhuyenMai` = 16 tệp Java**. Bản này giữ đúng danh sách đó. Nếu giảng viên bắt buộc đủ 17 kiểu dữ liệu, nhóm cần thống nhất bổ sung một lớp; đây chưa phải yêu cầu đã rõ trong tài liệu.

## Chạy nhanh trên Windows

1. Giải nén toàn bộ ZIP vào một thư mục, ví dụ `D:\DoAn\QuanLyQuanCaPhe`.
2. Kiểm tra trong Terminal: `java -version` và `javac -version`. Cần **JDK 17 trở lên** để sửa và biên dịch.
3. Mở `run.bat`. Script tự biên dịch tất cả tệp trong `src`, sau đó mở menu.
4. Mở `test.bat` để chạy kiểm thử; kết quả bàn giao là **43/43 ca đạt, 177 phép kiểm tra**.

Muốn chạy ngay bản đã biên dịch: mở `run-jar.bat` hoặc chạy `java -Dfile.encoding=UTF-8 -jar dist/QuanLyQuanCaPhe.jar`. Cách này cần Java 17 trở lên nhưng không cần trình biên dịch. **Sau khi sửa nguồn, dùng `run.bat`**; JAR đi kèm là bản tại thời điểm bàn giao và không tự cập nhật.

Nếu Windows báo không nhận diện `java` hoặc `javac`, kiểm tra đã cài **JDK**, thêm thư mục `bin` của JDK vào PATH rồi mở lại Terminal. Nếu chữ có dấu hiển thị sai, dùng Windows Terminal, chạy `chcp 65001` và giữ mã nguồn ở UTF-8. Các script `.bat` đã đổi code page sang UTF-8.

## Chạy bằng Terminal

Mở Terminal ở thư mục chứa README này. Lệnh biên dịch và chạy:

```sh
javac -encoding UTF-8 --release 17 -Xlint:all -d out src/*.java
java -Dfile.encoding=UTF-8 -cp out Main
java -Dfile.encoding=UTF-8 -cp out KiemThu
```

Trên Windows có thể dùng `src\*.java`; các script đã viết sẵn. Trên macOS/Linux: `sh run.sh` và `sh test.sh`.

Để thử một bộ dữ liệu riêng:

```sh
java -Dfile.encoding=UTF-8 -cp out Main --data data/demo_rieng.txt
```

Nếu file chỉ định chưa tồn tại, chương trình tạo dữ liệu mẫu. Nếu file đã có nhưng bị lỗi, chương trình báo lỗi và dừng, không tự ghi đè bằng dữ liệu mẫu.

## Chạy trong VS Code hoặc NetBeans

**VS Code:** mở cả thư mục `QuanLyQuanCaPhe`, mở Terminal và chạy lệnh ở trên. Đây là cách không phụ thuộc extension. Nếu đã có hỗ trợ Java, có thể chạy phương thức `main` của `Main.java` hoặc `KiemThu.java`; đặt working directory là thư mục gốc dự án.

**NetBeans:** tạo Java Application dùng JDK 17 trở lên; chép nội dung `src` vào Source Packages ở default package. Đặt Main Class là `Main`, encoding UTF-8 và working directory là thư mục dự án. Không thêm dòng `package` vào riêng một tệp. Chạy `KiemThu.java` để kiểm tra logic. Đường dẫn file dữ liệu được in khi khởi động để tránh nhầm vị trí.

## Menu và chức năng

| Menu | Chức năng |
|---|---|
| 1 | Xem/thêm/sửa/xóa sản phẩm; tìm mã/tên; lọc đồ uống, thực phẩm, còn bán, hết hàng, hết hạn |
| 2 | Nhập thêm tồn kho theo mã sản phẩm |
| 3 | Xem/thêm/sửa/xóa/tìm khách hàng |
| 4 | Xem/thêm/sửa/xóa/tìm nhân viên |
| 5 | Giỏ hàng: thêm món, đổi số lượng, xóa món, xem tạm tính, thanh toán hoặc bỏ giỏ |
| 6 | Danh sách, chi tiết và hủy toàn bộ hóa đơn |
| 7 | Doanh thu toàn bộ/theo khoảng ngày; đếm hóa đơn; số lượng bán ròng toàn bộ |
| 8 | Lưu dữ liệu thủ công |
| 9 | Đọc lại dữ liệu từ file, có xác nhận trước khi bỏ thay đổi chưa lưu |
| 0 | Thoát, thử lưu nếu còn thay đổi |

Mỗi tác vụ quản lý hoàn tất sẽ trở về menu chính; riêng giỏ hàng có vòng lặp riêng. Ngày nhập theo `dd/MM/yyyy`, tiền nhập bằng số nguyên đồng, ví dụ `25000`, không nhập `25.000`.

## Dữ liệu mẫu và giao dịch đầu tiên

Lần đầu chạy có 5 sản phẩm, 2 nhân viên, 2 khách hàng, chưa có hóa đơn. Ngày hết hạn được tính từ ngày chạy đầu tiên; các lần mở sau giữ nguyên hạn đã lưu.

| Mã | Nội dung | Giá gốc | Giảm | Giá bán | Tồn đầu |
|---|---|---:|---:|---:|---:|
| DU01 | Cà phê sữa | 25.000 | 0% | 25.000 | 40 |
| DU02 | Trà đào | 30.000 | 10% | 27.000 | 30 |
| TP01 | Bánh mì | 20.000 | 0% | 20.000 | 20 |
| TP02 | Bánh ngọt hết hạn | 15.000 | 0% | 15.000 | 5 |
| DU03 | Nước suối hết hàng | 10.000 | 0% | 10.000 | 0 |

Nhân viên: `NV01`, `NV02`. Khách hàng: `KH01`, `KH02`. Tên và điện thoại mẫu là dữ liệu minh họa.

Ví dụ: menu **5**, thêm `DU01` số lượng `2`, thêm `TP01` số lượng `1`, chọn thanh toán, nhân viên `NV01`, khách `KH01`, tiền khách đưa `100000`, xác nhận `y`. Kết quả: **70.000đ tiền hàng, 30.000đ tiền thừa**, kho DU01 còn 38 và TP01 còn 19. Menu 6 hủy hóa đơn sẽ hoàn kho về 40 và 20, doanh thu của giao dịch này về 0.

## Tài liệu để học và bảo vệ

| Tệp | Nội dung |
|---|---|
| `HUONG_DAN.html` | Bản hướng dẫn gộp, mở bằng trình duyệt để đọc/in |
| `docs/01_YEU_CAU_VA_QUY_TAC.md` | Đối chiếu đề bài, quy tắc nghiệp vụ và phạm vi |
| `docs/02_PHAN_CONG.md` | Chi tiết việc của Thanh Dương, Tấn Viên, Thủy Tiên, Đức Phúc, Đình Triệu |
| `docs/03_THIET_KE_OOP.md` | 16 tệp, thuộc tính, hàm, quan hệ, giải thích 4 tính chất OOP |
| `docs/04_DEMO_VA_BAO_VE.md` | Kịch bản trình diễn và câu hỏi kèm gợi ý trả lời |
| `docs/05_LUU_TRU_VA_KIEM_THU.md` | Định dạng file, khôi phục, danh mục kiểm thử và kết quả thực tế |
| `docs/KET_QUA_KIEM_THU.txt` | Log chạy KiemThu khi bàn giao |
| `docs/KET_QUA_CONSOLE.txt` | Tóm tắt các kịch bản chạy menu thực tế |

## Các quy tắc cần biết trước khi chạy

- Hàng còn hạn trong chính ngày HSD; bị chặn từ ngày sau. Hết hàng và hết hạn được kiểm tra riêng.
- Giỏ chưa thanh toán không làm thay đổi kho. Thanh toán lỗi không tạo hóa đơn, không trừ một phần kho.
- Hóa đơn lưu tên, mã, số lượng, giá bán và tên người tại lúc giao dịch; sửa thông tin hiện tại không đổi lịch sử.
- Hủy toàn bộ một lần: hoàn kho, loại doanh thu; tiền cần hoàn khách bằng tổng tiền hàng.
- Không xóa sản phẩm/khách/nhân viên đã được bất kỳ hóa đơn nào tham chiếu, kể cả hóa đơn hủy.
- Một mã sản phẩm có một hạn sử dụng. Nhập hàng khác hạn phải tạo mã mới. Đây là mô hình đơn giản theo đề, chưa quản lý nhiều lô.
- Chương trình dùng một tiến trình console; chưa có đăng nhập, phân quyền, giao diện đồ họa hoặc đồng bộ nhiều máy.

## Lưu dữ liệu

Tự lưu sau mỗi thay đổi thành công vào `data/cuahang.txt`. File `.bak` giữ phiên bản ngay trước lần ghi gần nhất. Không sửa file trực tiếp trong lúc chương trình đang chạy. Kiểm thử dùng thư mục tạm riêng và tự dọn, không sử dụng dữ liệu bán hàng thật của bạn.

Nếu muốn làm lại dữ liệu mẫu, chạy với một đường dẫn `--data` mới. Nếu muốn khôi phục bản sao lưu, xem `docs/05_LUU_TRU_VA_KIEM_THU.md` để kiểm tra bản sao trước khi thay file chính.

## Kết quả xác minh

Đã biên dịch bằng OpenJDK 17.0.20 với `--release 17 -Xlint:all`, không có cảnh báo; đã chạy kiểm thử nghiệp vụ, lưu/đọc và nhập liệu. Script Windows được cung cấp nhưng môi trường kiểm tra hiện tại là Linux; nhóm cần chạy `run.bat` và `test.bat` trên máy Windows sẽ dùng để thuyết trình.

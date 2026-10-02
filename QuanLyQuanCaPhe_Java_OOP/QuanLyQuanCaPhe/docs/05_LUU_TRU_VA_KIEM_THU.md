# Lưu trữ dữ liệu và kết quả kiểm thử

## File text chứa những gì

Đường dẫn mặc định `data/cuahang.txt`, tính theo thư mục làm việc khi chạy. Các script chuyển vào thư mục gốc dự án trước khi gọi Java. Có thể truyền `--data` để dùng file riêng.

Định dạng là **Java Properties text UTF-8**, đọc bằng Reader và ghi bằng Writer. Đây là file văn bản gồm khóa và giá trị, không phải serialization nhị phân. Properties có sẵn trong JDK và tự xử lý ký tự đặc biệt như dấu bằng hoặc dấu gạch chéo ngược. Thứ tự các dòng khi ghi không phải là hợp đồng; việc đọc dựa trên tên khóa.

| Nhóm khóa | Dữ liệu |
|---|---|
| phienBan | Phiên bản định dạng, hiện là 1 |
| sanPham.soLuong | Số sản phẩm |
| sanPham.i.loai/ma/ten/giaGoc/tonKho/giamGia/hanSuDung | Toàn bộ dữ liệu sản phẩm thứ i |
| khachHang.soLuong và khachHang.i.ma/ten/sdt | Danh sách khách |
| nhanVien.soLuong và nhanVien.i.ma/ten/sdt | Danh sách nhân viên |
| hoaDon.soLuong | Số hóa đơn, gồm cả đã hủy |
| hoaDon.i.ma/ngay/maKhach/tenKhach/maNhanVien/tenNhanVien | Thông tin định danh và snapshot người |
| hoaDon.i.tienKhachDua/daHuy | Tiền khách đưa và trạng thái |
| hoaDon.i.chiTiet.soLuong | Số dòng hóa đơn i |
| hoaDon.i.chiTiet.j.ma/ten/soLuong/donGia | Snapshot dòng j của hóa đơn i |

`i`, `j` là chỉ số bắt đầu từ 0. Ví dụ dưới đây chỉ minh họa một phần file, không phải một file hoàn chỉnh để thay thế dữ liệu:

```properties
phienBan=1
sanPham.0.loai=DO_UONG
sanPham.0.ma=DU01
sanPham.0.ten=Cà phê sữa
sanPham.0.giaGoc=25000
sanPham.0.tonKho=38
sanPham.0.giamGia=0
sanPham.0.hanSuDung=2026-12-31
hoaDon.0.ma=HD000001
hoaDon.0.chiTiet.0.ma=DU01
hoaDon.0.chiTiet.0.ten=Cà phê sữa
hoaDon.0.chiTiet.0.soLuong=2
hoaDon.0.chiTiet.0.donGia=25000
```

Ngày trong menu là `dd/MM/yyyy`, nhưng file dùng ISO `yyyy-MM-dd` và ngày giờ ISO cho hóa đơn. Số tiền và số lượng ghi số nguyên không có dấu phân tách hàng nghìn. Mã khách rỗng là vãng lai; `daHuy` chỉ chấp nhận `true` hoặc `false`.

## Trình tự ghi

1. Chuyển trạng thái hiện tại thành Properties.
2. Tạo thư mục đích nếu chưa có và ghi vào file tạm cùng thư mục.
3. Nếu file chính tồn tại, sao chép nó thành file `.bak`.
4. Đổi tên file tạm thay file chính; ưu tiên atomic move nếu hệ thống tệp hỗ trợ.
5. Dọn file tạm còn lại; nếu lỗi, thông báo để người dùng biết chưa lưu.

`.bak` chỉ giữ **một phiên bản trước lần ghi gần nhất**, không phải lịch sử đầy đủ. Một lần lưu thủ công tiếp theo cũng cập nhật bản sao này. Đừng coi `.bak` là bản sao lưu dài hạn bất biến.

## Trình tự đọc

Đọc Properties, kiểm tra phiên bản và trường bắt buộc, dựng sản phẩm và người trước, sau đó dựng ChiTietHoaDon và HoaDon. Kiểm tra tham chiếu mã sản phẩm/người và mã không trùng. Tồn kho được đọc nguyên giá trị hiện tại; không bán/hủy lại hóa đơn đã lưu.

Đối tượng CuaHang được xây dựng mới ở trong `LuuTru.doc`. Main chỉ thay cửa hàng đang dùng sau khi hàm trả về thành công. Nếu file có lỗi, cửa hàng đang sử dụng không bị thay bởi một bộ dữ liệu đọc dở.

Các trường số đếm khi đọc giới hạn tối đa 100.000 bản ghi cho mỗi nhóm hoặc chi tiết một hóa đơn để chặn số đếm vô lý. Giới hạn này phù hợp bài tập nhỏ; hệ thống không được thiết kế cho kho dữ liệu lớn. Không có checksum, xác thực nguồn file hay công cụ tự sửa file bị thay đổi thủ công.

## Khôi phục bản sao lưu

1. Đóng chương trình đang dùng file cần khôi phục.
2. Sao chép file `.bak` sang một file khác, ví dụ `data/phuc_hoi_thu.txt`; giữ nguyên cả file chính bị lỗi và bản `.bak` để có thể quay lại.
3. Chạy `Main --data data/phuc_hoi_thu.txt`, xem sản phẩm, hóa đơn và doanh thu. Thao tác trên bản sao, không mở `.bak` trực tiếp để bán hàng.
4. Nếu dữ liệu đúng, đóng chương trình. Lưu riêng bản file chính cũ, rồi dùng bản đã kiểm tra thay `data/cuahang.txt`.
5. Mở lại bình thường và kiểm tra lần nữa.

Nếu bản `.bak` cũng lỗi hoặc thiếu giao dịch gần nhất, không thể đảm bảo phục hồi toàn bộ từ chương trình này. Chưa có nhật ký giao dịch để dựng lại các thay đổi chưa lưu.

## Cách chạy bộ kiểm thử

Windows: `test.bat`. macOS/Linux: `sh test.sh`. Hoặc sau biên dịch:

```sh
java -Dfile.encoding=UTF-8 -cp out KiemThu
```

Mỗi dòng `[PASS]` tương ứng một ca. Nếu có `[FAIL]`, xem mô tả và lỗi cuối dòng; chương trình trả exit code 1. `KiemThu` tự kiểm tra bằng hàm `dung`, không dựa vào từ khóa Java `assert`, nên không cần cờ `-ea`.

Kiểm thử thời gian dùng đồng hồ cố định ngày 02/01/2030. Kiểm thử file tạo thư mục tạm riêng, rồi dọn bằng `finally`; không ghi `data/cuahang.txt`. Kiểm thử nhập liệu sử dụng ByteArrayInputStream để mô phỏng người gõ, không cần thao tác tay.

## Danh mục ca kiểm thử

| Ca | Nội dung xác minh |
|---|---|
| 01–04 | Chuẩn hóa mã/tên, miền giá/tồn/giảm và làm tròn tiền |
| 05–06 | Đa hình, interface và ranh giới HSD của cả hai lớp con |
| 07–11 | Trùng mã, tìm kiếm, lọc, sửa đúng ràng buộc, xóa sản phẩm |
| 12–14 | Regex SĐT, nhân sự, cập nhật không dở dang và giới hạn nhập kho |
| 15–19 | Giỏ lỗi, thiếu kho, hết hạn, người không tồn tại, thiếu tiền |
| 20–22 | Giao dịch nhiều món, tiền thừa, mã trùng sau chuẩn hóa, giảm 100% |
| 23–25 | Snapshot giá/tên người, không bị sửa theo giỏ sau thanh toán |
| 26–28 | Hủy, hủy lặp, hoàn kho vượt trần không thay đổi dở dang |
| 29–30 | Chặn xóa dữ liệu có tham chiếu; cho xóa người chưa có giao dịch |
| 31–33 | Doanh thu theo ngày, số lượng bán ròng và mã hóa đơn sau hủy |
| 34–35 | Danh sách chỉ đọc, số tiền lớn hơn miền int |
| 36–37 | Nhập số sai/vượt long, EOF, ngày thật/năm nhuận, SĐT và xác nhận |
| 38–39 | Lưu/đọc UTF-8, ký tự đặc biệt, snapshot, hủy, mã tiếp theo, cửa hàng trống |
| 40 | File sai phiên bản/số/loại/ngày/SĐT/trạng thái/tham chiếu, thiếu trường, cắt ngắn, escape sai |
| 41–42 | Ghi thất bại giữ file chính và dọn file tạm; đọc file không có |
| 43 | HoaDon copy danh sách, chặn hai chi tiết cùng mã |

## Kết quả chạy thực tế

Môi trường kiểm tra: **OpenJDK 17.0.20 trên Linux, UTF-8**, biên dịch mục tiêu Java 17 với `-Xlint:all`, không có cảnh báo. Kết quả: **43/43 ca đạt, 177 phép kiểm tra, 0 ca lỗi**. Log đầy đủ ở `KET_QUA_KIEM_THU.txt`.

Ngoài bộ kiểm thử lớp, đã đưa chuỗi nhập vào Main để chạy các luồng console: bán nhiều món, xem báo cáo, khởi động lại, hủy và hủy lặp, nhập menu sai, hết hạn/hết kho/thiếu tiền, CRUD người/sản phẩm, nhập kho, tìm/lọc, lưu/nạp lại và EOF. File lỗi lúc khởi động được giữ nguyên. Tóm tắt ở `KET_QUA_CONSOLE.txt`.

Script `.bat` được soạn cho Windows nhưng chưa được thực thi trên Windows trong môi trường bàn giao. Cần chạy lại trên máy bảo vệ để xác nhận JDK, PATH, font console và quyền ghi. Kiểm thử không khẳng định chương trình không thể có lỗi trong mọi tình huống; nó là bằng chứng cho những đường nghiệp vụ đã kiểm tra.

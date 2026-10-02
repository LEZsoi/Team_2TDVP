# Thiết kế lớp và giải thích hướng đối tượng

## Cách đọc mã nguồn

Đọc theo thứ tự: QuyTac → KhuyenMai → SanPham → DoUong/ThucPham → DanhSachSanPham → Nguoi → KhachHang/NhanVien → ChiTietHoaDon → HoaDon → CuaHang → NhapLieu → LuuTru → Main → KiemThu. Thứ tự này giúp hiểu những kiểu dữ liệu trước khi đọc hàm sử dụng chúng.

Tất cả tệp ở `src`, dùng default package để dễ biên dịch trong môn học. Không có framework, reflection, annotation tùy chỉnh, cơ sở dữ liệu hoặc phụ thuộc tải qua mạng. Lambda xuất hiện chủ yếu trong kiểm thử; nghiệp vụ chính dùng vòng lặp và điều kiện thông thường.

## Danh mục lớp

| Tệp/kiểu | Dữ liệu hoặc vai trò chính | Phương thức đáng đọc |
|---|---|---|
| QuyTac, final | Hằng số, kiểm tra và chuẩn hóa | chuanHoaMa, docNgay, dinhDangTien |
| NhapLieu, final | Scanner và PrintStream | soNguyen, soNguyenDai, ngay, xacNhan |
| KhuyenMai, interface | Hợp đồng giá bán thực tế | giaBanThucTe |
| SanPham, abstract | ma, ten, giaGoc, tonKho, phanTramGiamGia | giaBanThucTe, nhapKho, xuatKho, duocBan |
| ThucPham, final | hanSuDung | getLoai, getHanSuDung, duocBan |
| DoUong, final | hanSuDung | getLoai, getHanSuDung, duocBan |
| DanhSachSanPham, final | ArrayList các SanPham | them, sua, xoa, timTheoMa, timTheoTen, locTheoTrangThai |
| Nguoi, abstract | ma, ten, soDienThoai | capNhat, getVaiTro |
| KhachHang, final | Kế thừa dữ liệu Nguoi | constructor, getVaiTro |
| NhanVien, final | Kế thừa dữ liệu Nguoi | constructor, getVaiTro |
| ChiTietHoaDon, final | maSanPham, tenSanPham, soLuong, donGia | constructor snapshot, thanhTien |
| HoaDon, final | mã, ngày giờ, người, chi tiết, tiền đưa, daHuy | tongTien, tienThua, getTrangThai |
| CuaHang, final | Sản phẩm, khách, nhân viên, hóa đơn, đồng hồ | banHang, huyHoaDon, doanhThu |
| LuuTru, final | Hàm đọc/ghi text, không giữ trạng thái riêng | ghi, doc |
| Main, final | CuaHang, NhapLieu, đường dẫn và trạng thái chưa lưu | main, chay, các menu |
| KiemThu, final | Ca kiểm thử và số đếm kết quả | main, các hàm kiểm thử file |

## Quan hệ giữa các kiểu

| Nguồn | Quan hệ | Đích | Ý nghĩa |
|---|---|---|---|
| SanPham | implements | KhuyenMai | Cam kết cung cấp giá bán sau giảm |
| ThucPham, DoUong | extends | SanPham | Dùng chung dữ liệu/hành vi sản phẩm |
| KhachHang, NhanVien | extends | Nguoi | Dùng chung thông tin định danh |
| CuaHang | sở hữu một | DanhSachSanPham | Quản lý thực đơn |
| DanhSachSanPham | chứa 0..n | SanPham | Một danh sách có cả đồ uống và thực phẩm |
| CuaHang | chứa 0..n | KhachHang, NhanVien, HoaDon | Trạng thái hoạt động của cửa hàng |
| HoaDon | chứa 1..n | ChiTietHoaDon | Một hóa đơn có ít nhất một dòng hàng |
| ChiTietHoaDon | sao chép giá trị từ | SanPham | Không giữ tham chiếu sống tới giá menu |
| Main | gọi | NhapLieu, CuaHang, LuuTru | Giao diện console điều phối thao tác |
| LuuTru | tạo/đọc | CuaHang và các mô hình | Phục hồi trạng thái từ text |
| KiemThu | sử dụng | Các lớp còn lại | Xác minh kết quả và tình huống lỗi |

## Đóng gói

Đóng gói là giữ trạng thái bên trong đối tượng và cung cấp đường thay đổi hợp lệ. Ví dụ `tonKho` là private, không có setter tùy ý. Muốn nhập thêm hàng phải đi qua kiểm tra số lượng dương và giới hạn tồn.

```java
private int tonKho;

void xuatKho(int soLuong) {
    QuyTac.yeuCau(soLuong > 0 && soLuong <= tonKho,
            "Số lượng xuất không hợp lệ.");
    tonKho -= soLuong;
}
```

Hàm trên có phạm vi package để lớp nghiệp vụ cùng dự án gọi. Đây là ràng buộc tổ chức mã, không phải cơ chế bảo mật chống mã độc cùng package. Các getter danh sách trả bản sao chỉ đọc để bên ngoài không thêm/xóa trực tiếp phần tử. Các phần tử sản phẩm/người vẫn có những hành vi cập nhật được cho phép; không gọi đó là bất biến sâu của toàn cửa hàng.

## Kế thừa

DoUong và ThucPham đều có mã, tên, giá, tồn, giảm giá nên dùng lớp cha SanPham. KhachHang và NhanVien đều có mã, tên, điện thoại nên dùng Nguoi. Constructor lớp con gọi `super(...)` để lớp cha kiểm tra và gán phần dữ liệu chung.

```java
public DoUong(String ma, String ten, long gia, int ton, int giam, LocalDate han) {
    super(ma, ten, gia, ton, giam);
    this.hanSuDung = Objects.requireNonNull(han, "Thiếu hạn sử dụng.");
}
```

`super` ở đây không tạo thêm một sản phẩm riêng. Nó khởi tạo phần lớp cha bên trong cùng đối tượng DoUong.

## Trừu tượng

SanPham và Nguoi là abstract: không được tạo đối tượng trực tiếp, nhưng dùng làm kiểu chung. SanPham yêu cầu lớp con có `getLoai`, `getHanSuDung`, `duocBan(LocalDate)`; Nguoi yêu cầu `getVaiTro`.

KhuyenMai là interface: định nghĩa hợp đồng `giaBanThucTe()`. SanPham hiện thực công thức chung, DoUong/ThucPham kế thừa cách tính đó. Trong bản này không ép các lớp con ghi đè công thức giống hệt nhau chỉ để tăng số phương thức.

## Đa hình

Biến kiểu cha có thể giữ đối tượng lớp con. Khi gọi phương thức được ghi đè, Java dùng phiên bản của lớp thực tế.

```java
SanPham sp = new DoUong("DU10", "Trà", 30000, 10, 10, LocalDate.of(2099, 12, 31));
System.out.println(sp.getLoai());       // DO_UONG
System.out.println(sp.giaBanThucTe());  // 27000

KhuyenMai km = sp;
System.out.println(km.giaBanThucTe());  // gọi qua kiểu interface

Nguoi nguoi = new NhanVien("NV10", "An", "0901234567");
System.out.println(nguoi.getVaiTro());  // Nhân viên
```

DanhSachSanPham dùng `ArrayList<SanPham>` nên cùng danh sách chứa cả hai lớp con. CuaHang gọi `sp.duocBan(ngay)` mà không cần tự xử lý từng loại bằng một chuỗi if-else trong nghiệp vụ bán.

## Overriding và overloading

**Overriding:** DoUong ghi đè `getLoai`, `getHanSuDung` và `duocBan(LocalDate)` của SanPham; có `@Override` để trình biên dịch kiểm tra chữ ký.

**Overloading:** `duocBan()` và `duocBan(LocalDate)` cùng tên nhưng khác tham số. ChiTietHoaDon cũng có hai constructor: một nhận SanPham khi bán, một nhận các trường snapshot khi đọc file. Không nhầm nạp chồng với ghi đè.

## Vì sao snapshot cần thiết

Nếu ChiTietHoaDon chỉ giữ một đối tượng SanPham rồi mỗi lần in lại gọi giá hiện tại, hóa đơn cũ có thể đổi tổng khi người dùng sửa menu. Bản này giữ `String ma`, `String ten`, `int soLuong`, `long donGia` ở các trường final. Đơn giá là giá sau khuyến mãi tại thời điểm thanh toán.

```java
HoaDon hd = cuaHang.banHang("KH01", "NV01", Map.of("DU01", 1), 50000);
long giaCu = hd.getChiTiet().get(0).getDonGia();
// Sau khi sửa giá DU01 qua danh sách sản phẩm,
// giaCu và hd.tongTien() vẫn phản ánh giao dịch ban đầu.
```

LuuTru ghi chính đơn giá snapshot đó. Khi nạp hóa đơn, không tính lại từ SanPham và không gọi banHang, vì kho trong file đã là kho sau giao dịch.

## Luồng thanh toán

1. Main tạo giỏ theo mã và số lượng; xem giỏ không thay đổi kho.
2. CuaHang xác minh nhân viên và khách, lấy một thời điểm lập hóa đơn.
3. Kiểm tra mọi dòng: mã có thật, không lặp sau chuẩn hóa, số lượng dương, còn hạn, đủ tồn.
4. Tạo ChiTietHoaDon từ giá tại thời điểm này; HoaDon kiểm tra danh sách và tiền khách đưa.
5. Khi mọi điều kiện hợp lệ, mới trừ kho từng dòng và thêm hóa đơn vào lịch sử.
6. Main in kết quả rồi gọi LuuTru để lưu toàn bộ trạng thái.

Các bước 2–4 thất bại thì bước 5 chưa chạy. Việc ghi file thất bại sau bước 5 được báo riêng: giao dịch đã ở bộ nhớ nhưng chưa lưu được. Người dùng có thể thử lưu lại.

## Luồng hủy hóa đơn

1. Tìm hóa đơn, từ chối nếu không có hoặc đã hủy.
2. Kiểm tra tất cả sản phẩm vẫn tồn tại và việc hoàn kho không vượt giới hạn.
3. Hoàn số lượng từng dòng vào kho.
4. Đánh dấu hóa đơn hủy và trả số tiền hàng cần hoàn.
5. Khi thống kê, bỏ qua hóa đơn đã hủy; lịch sử vẫn hiển thị tổng gốc và trạng thái.

## Quy tắc xử lý lỗi

| Lỗi | Cách xử lý |
|---|---|
| Nhập số/ngày/điện thoại sai | NhapLieu thông báo và cho nhập lại |
| Nghiệp vụ sai như thiếu tiền hoặc hết hàng | CuaHang ném IllegalArgumentException; Main in lý do |
| Phép tính vượt miền long | Math.addExact/multiplyExact ném ArithmeticException |
| Đọc/ghi file lỗi | LuuTru ném IOException; Main báo và giữ trạng thái phù hợp |
| Hết luồng nhập | NoSuchElementException được Main bắt để thoát an toàn |

`Objects.requireNonNull` còn giúp phát hiện lỗi lập trình khi truyền thiếu đối tượng bắt buộc. Không phải mọi lỗi lập trình đều bị nuốt bằng `catch(Exception)`; các đường nhập thông thường được kiểm tra rõ trước khi gọi nghiệp vụ.

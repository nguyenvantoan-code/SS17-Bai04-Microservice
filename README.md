# BÀI TẬP 4: CHỐNG CACHE STAMPEDE VỚI THUỘC TÍNH SYNC

## 1. Cơ chế hoạt động của Thuộc tính Sync
Trong Java Spring Cache, `sync = true` có nghĩa là "Đồng bộ hóa các truy cập tới vùng nhớ đệm tại cấp độ Key". 
Khi Cache hết hạn (hoặc chưa có - Cold Start), nếu có 50 request ập đến cùng lúc gọi truy vấn cho cùng một mã sản phẩm:
- Thread đầu tiên tới sẽ giành được "Khóa" (Mutex Lock). Nó đi xuống DB để lấy dữ liệu.
- 49 Thread còn lại sẽ bị chặn lại (Block) và chờ đợi.
- Sau khoảng 2 giây, Thread 1 lấy xong dữ liệu từ DB, nó ghi vào Cache rồi mở khóa.
- Lúc này, 49 Thread kia tỉnh dậy và lập tức nhận được giá trị từ Cache mà không phải truy vấn DB.

*Kết quả*: Chỉ có **DUY NHẤT 1 kết nối** được gọi xuống Database, cứu Database khỏi hiện tượng Cache Stampede (Bão truy cập/Thundering Herd).

## 2. Kết quả kiểm thử đa luồng (Concurrent Testing)
Trong file `FlashSaleConcurrentTest.java`, CompletableFuture được thiết lập gửi 50 luồng truy cập đồng thời.
- **Log in ra**: Bất chấp có 50 yêu cầu, dòng chữ `Fetching from Database for product 999` chỉ xuất hiện đúng **1 lần duy nhất** trên màn hình Console. 
- **Tổng thời gian xử lý**: Mất khoảng ~2050ms cho tổng số 50 request, thay vì `50 * 2s = 100s` nếu truy vấn tuần tự không có cache, hoặc đánh sập DB nếu bắn 50 luồng query nặng cùng lúc không có đồng bộ.

*(Đính kèm ảnh chụp log test thể hiện 1 dòng Fetching from Database và 50 dòng kết quả)*

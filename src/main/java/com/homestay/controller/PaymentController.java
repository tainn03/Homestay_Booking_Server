package com.homestay.controller;

import com.homestay.service.BookingService;
import com.homestay.service.external.VNPayService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentController {
    VNPayService vnPayService;
    BookingService bookingService;

    // Chuyển hướng người dùng đến cổng thanh toán VNPAY
    @GetMapping("/order")
    public void submitOrder(@RequestParam("amount") int amount,
                            @RequestParam("orderInfo") String orderInfo,
                            HttpServletRequest request, HttpServletResponse response) throws IOException {
        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        String vnpayUrl = vnPayService.createOrder(amount, orderInfo, baseUrl);

        // Điều hướng người dùng sang trang thanh toán của VNPAY
        response.sendRedirect(vnpayUrl);
    }


    // Sau khi hoàn tất thanh toán, VNPAY sẽ chuyển hướng trình duyệt về URL này
    @GetMapping("/vnpay-payment")
    public void handleVNPayPayment(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int paymentStatus = vnPayService.orderReturn(request);

        String orderInfo = request.getParameter("vnp_OrderInfo");
        LocalDateTime paymentTime =
                LocalDateTime.parse(request.getParameter("vnp_PayDate"),
                        DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String transactionId = request.getParameter("vnp_TransactionNo");
        String totalPrice = String.valueOf(Integer.parseInt(request.getParameter("vnp_Amount")) / 100);
        String vnp_BankCode = request.getParameter("vnp_BankCode");
        String vnp_BankTranNo = request.getParameter("vnp_BankTranNo");
        String vnp_CardType = request.getParameter("vnp_CardType");
        String vnp_TxnRef = request.getParameter("vnp_TxnRef");
        String vnp_SecureHash = request.getParameter("vnp_SecureHash");

        // Tạo thanh toán mới cho đơn đặt phòng
        bookingService.createPayment(orderInfo, paymentTime, paymentStatus, totalPrice, transactionId, vnp_BankCode, vnp_BankTranNo, vnp_CardType, vnp_TxnRef, vnp_SecureHash);

        // Chuyển hướng người dùng đến ReactJS client qua URL dựa trên kết quả thanh toán
        String redirectUrl = "http://localhost:3000/bookingdetail?id=" + orderInfo;
        response.sendRedirect(redirectUrl);
    }
}

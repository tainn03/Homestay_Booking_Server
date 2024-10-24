package com.homestay.controller;

import com.homestay.service.external.VNPayService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VNPayController {
    VNPayService vnPayService;

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
    public ResponseEntity<Map<String, Object>> handleVNPayPayment(HttpServletRequest request) {
        int paymentStatus = vnPayService.orderReturn(request);

        // Lấy các thông tin từ request sau khi thanh toán từ VNPay
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

        // Tạo một đối tượng Map để chứa các thông tin thanh toán
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("orderId", orderInfo);
        responseBody.put("totalPrice", totalPrice);
        responseBody.put("paymentTime", paymentTime);
        responseBody.put("transactionId", transactionId);
        responseBody.put("paymentStatus", paymentStatus == 1 ? "success" : "fail");
        responseBody.put("vnp_BankCode", vnp_BankCode);
        responseBody.put("vnp_BankTranNo", vnp_BankTranNo);
        responseBody.put("vnp_CardType", vnp_CardType);
        responseBody.put("vnp_TxnRef", vnp_TxnRef);
        responseBody.put("vnp_SecureHash", vnp_SecureHash);

        // Chuyển hướng người dùng đến ReactJS client qua URL dựa trên kết quả thanh toán
        String redirectUrl = "http://localhost:3000/pay/" + (paymentStatus == 1 ? "ordersuccess" : "orderfail");
        responseBody.put("redirectUrl", redirectUrl);

        // Trả về thông tin và URL chuyển hướng dưới dạng JSON
        return ResponseEntity.ok(responseBody);
    }
}

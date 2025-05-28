package com.example.nammoadidaphat.presentation.ui.profile

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class FAQItem(
    val question: String,
    val answer: String
)

data class HelpUiState(
    val faqItems: List<FAQItem> = emptyList()
)

@HiltViewModel
class HelpViewModel @Inject constructor() : ViewModel() {
    
    private val _uiState = MutableStateFlow(
        HelpUiState(
            faqItems = createFaqItems()
        )
    )
    val uiState: StateFlow<HelpUiState> = _uiState.asStateFlow()
    
    private fun createFaqItems(): List<FAQItem> {
        return listOf(
            FAQItem(
                question = "Làm thế nào để theo dõi tiến độ của tôi?",
                answer = "Bạn có thể theo dõi tiến độ thông qua màn hình \"Báo cáo\". Tại đây, bạn sẽ thấy các số liệu về hoạt động, lịch sử tập luyện, và tiến bộ của mình theo thời gian."
            ),
            FAQItem(
                question = "Làm cách nào để thay đổi thông tin cá nhân?",
                answer = "Vào mục \"Cá nhân\" > \"Chỉnh sửa hồ sơ\" để cập nhật thông tin cá nhân như ảnh đại diện, tên, chiều cao, cân nặng và các mục tiêu tập luyện."
            ),
            FAQItem(
                question = "Làm thế nào để điều chỉnh cường độ bài tập?",
                answer = "Khi chọn bài tập, bạn có thể chọn mức độ từ Dễ đến Khó dựa trên khả năng hiện tại của mình. Ứng dụng sẽ điều chỉnh số lần lặp lại và độ khó của các bài tập phù hợp với mức độ bạn chọn."
            ),
            FAQItem(
                question = "Làm thế nào để đồng bộ dữ liệu với thiết bị khác?",
                answer = "Dữ liệu được tự động đồng bộ với tài khoản của bạn trên đám mây. Bạn chỉ cần đăng nhập với cùng một tài khoản trên thiết bị khác để truy cập dữ liệu của mình."
            ),
            FAQItem(
                question = "Làm thế nào để đặt thông báo nhắc nhở tập luyện?",
                answer = "Vào \"Cá nhân\" > \"Thông báo\" và bật tính năng thông báo. Tại đây bạn có thể đặt lịch và tùy chỉnh các thông báo nhắc nhở tập luyện theo lịch trình của mình."
            ),
            FAQItem(
                question = "Làm thế nào để nâng cấp lên tài khoản Premium?",
                answer = "Từ màn hình \"Cá nhân\", bạn có thể nhấn vào banner \"Nâng cấp lên Premium\" để xem các tùy chọn đăng ký và thanh toán có sẵn cho tài khoản Premium."
            ),
            FAQItem(
                question = "Ứng dụng có hoạt động offline không?",
                answer = "Có, hầu hết các tính năng của ứng dụng có thể hoạt động offline. Các bài tập đã tải xuống trước đó và lịch tập luyện của bạn vẫn có thể truy cập được ngay cả khi không có kết nối internet. Dữ liệu sẽ được đồng bộ khi bạn kết nối lại."
            ),
            FAQItem(
                question = "Làm thế nào để xóa tài khoản của tôi?",
                answer = "Để xóa tài khoản, vui lòng vào \"Cá nhân\" > \"Bảo mật\" > \"Xóa tài khoản\". Lưu ý rằng hành động này không thể hoàn tác và tất cả dữ liệu của bạn sẽ bị xóa vĩnh viễn."
            ),
            FAQItem(
                question = "Làm thế nào để đổi mật khẩu?",
                answer = "Đi đến \"Cá nhân\" > \"Bảo mật\" > \"Đổi mật khẩu\". Bạn sẽ cần nhập mật khẩu hiện tại và mật khẩu mới của mình."
            ),
            FAQItem(
                question = "Làm thế nào để báo cáo lỗi trong ứng dụng?",
                answer = "Nếu bạn gặp lỗi, vui lòng gửi email cho chúng tôi theo địa chỉ support@exs-fitness.com với mô tả chi tiết về lỗi và ảnh chụp màn hình nếu có thể. Chúng tôi sẽ cố gắng giải quyết vấn đề càng sớm càng tốt."
            )
        )
    }
} 
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.DocumentSnapshot

fun fetchWorkouts(selectedTab: String, onDataFetched: (List<workouts>) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    val workoutsRef = db.collection("workouts").document(selectedTab)  // Lấy tài liệu cho "lưng" hoặc "ngực"

    workoutsRef.get()
        .addOnSuccessListener { document: DocumentSnapshot ->
            if (document.exists()) {
                val workoutList = mutableListOf<workouts>() // Đảm bảo 'workouts' viết thường

                val data = document.data as Map<String, Any>

                // Lặp qua các mục dữ liệu trong Firestore
                for (entry in data) {
                    val workoutData = entry.value as Map<String, Any>

                    // Lấy giá trị từ Firestore với sự kiểm tra kiểu dữ liệu an toàn
                    val name = workoutData["name"] as? String ?: ""
                    val description = workoutData["description"] as? String ?: ""
                    val duration = workoutData["duration"] as? String ?: ""
                    val reps = (workoutData["reps"] as? Long)?.toInt() ?: 0
                    val sets = (workoutData["sets"] as? Long)?.toInt() ?: 0

                    // Thêm đối tượng workouts vào danh sách
                    workoutList.add(
                        workouts( // Thay đổi từ 'Workout' thành 'workouts'
                            name = name,
                            description = description,
                            duration = duration,
                            reps = reps,
                            sets = sets
                        )
                    )
                }
                // Gọi callback sau khi lấy dữ liệu thành công
                onDataFetched(workoutList)
            }
        }
        .addOnFailureListener { exception ->
            // Xử lý khi có lỗi
            println("Error getting documents: $exception")
        }
}

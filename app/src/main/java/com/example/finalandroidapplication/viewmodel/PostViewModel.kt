import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.content.Context
import androidx.lifecycle.LiveData
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class PostViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()

    private val _isPosted = MutableLiveData<Boolean>()
    val isPosted: LiveData<Boolean> get() = _isPosted

    fun uploadPost(
        post: String,
        imageUri: String?,
        userId: String,
        context: Context
    ) {
        val postId = UUID.randomUUID().toString()
        val timestamp = System.currentTimeMillis().toString()
        savePostToLocal(postId, post, imageUri, userId, timestamp, context)

        val postData = mapOf(
            "postId" to postId,
            "postDes" to post,
            "userId" to userId,
            "timestamp" to timestamp,
            "imageUri" to (imageUri ?: "")
        )

        firestore.collection("posts").document(postId)
            .set(postData)
            .addOnSuccessListener {
                _isPosted.postValue(true)
            }
            .addOnFailureListener { exception ->
                _isPosted.postValue(false)
            }
    }

    private fun savePostToLocal(
        postId: String,
        post: String,
        imageUri: String?,
        userId: String,
        timestamp: String,
        context: Context
    ) {
        val sharedPreferences = context.getSharedPreferences("LocalPosts", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(postId, "$post|$imageUri|$userId|$timestamp")
        editor.apply()
    }



}

import com.amazonaws.kinesisvideo.demoapp.network.models.GetWebRtcBEModel
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

const val HUALAI_BASE_URL = "https://test-telecom-app-api.hualaikeji.com"
private const val GET_WEBRTC = "/webrtc/v1/auth/get"
private const val USER_LOGIN = "/app/v1/user/login"

interface HualaiApi {

    @POST(GET_WEBRTC)
    fun getWebRTC(
        @Header("H-AccessToken") accessToken: String,
        @Body body: RequestBody
    ): GetWebRtcBEModel

}
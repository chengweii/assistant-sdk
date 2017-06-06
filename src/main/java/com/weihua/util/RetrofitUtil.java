package com.weihua.util;

import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Url;

public class RetrofitUtil {

	private static final String baseUrl = "https://www.weihua.com";
	private static Retrofit retrofit;
	public static IRetrofitService retrofitService;

	static {
		retrofit = new Retrofit.Builder().baseUrl(baseUrl).addConverterFactory(GsonConverterFactory.create()).build();
		retrofitService = retrofit.create(IRetrofitService.class);
	}

	public static RequestBody getJsonRequestBody(Object requestBody) throws Exception {
		String content = GsonUtil.toJson(requestBody);
		RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), content);
		return body;
	}

	public interface IRetrofitService {
		@POST
		public Call<ResponseBody> post(@Url String url, @Body RequestBody requestBody, @Header("Cookie") String cookie);

		@GET
		public Call<ResponseBody> get(@Url String url, @Header("Cookie") String cookie);
	}

	public static void main(String[] args) throws Exception {
		execute();
		enqueue();
	}

	public static void execute() throws Exception {
		LoginInfo loginInfo = new LoginInfo();
		loginInfo.username = "33@qq.com";
		loginInfo.password = "33";
		Call<ResponseBody> loginResult = RetrofitUtil.retrofitService.post(
				"https://dida365.com/api/v2/user/signon?wc=true&remember=true",
				RetrofitUtil.getJsonRequestBody(loginInfo), "");

		Response<ResponseBody> loginResponse = loginResult.execute();
		String loginContent = loginResponse.body().string();
		Map<String, String> map = GsonUtil.getMapFromJson(loginContent);

		Call<ResponseBody> taskResult = RetrofitUtil.retrofitService.get("https://dida365.com/api/v2/project/all/tasks",
				"t=" + map.get("token"));

		Response<ResponseBody> taskResponse = taskResult.execute();
		String taskContent = taskResponse.body().string();
		System.out.println(taskContent);
	}

	public static void enqueue() throws Exception {
		LoginInfo loginInfo = new LoginInfo();
		loginInfo.username = "33@qq.com";
		loginInfo.password = "33";

		Call<ResponseBody> result = retrofitService.post("https://dida365.com/api/v2/user/signon?wc=true&remember=true",
				getJsonRequestBody(loginInfo), "");

		result.enqueue(new Callback<ResponseBody>() {
			@Override
			public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
				try {
					String responseContent = response.body().string();
					System.out.println(responseContent);
					Map<String, String> map = GsonUtil.getMapFromJson(responseContent);
					Call<ResponseBody> result = retrofitService.get("https://dida365.com/api/v2/project/all/tasks",
							"t=" + map.get("token"));

					result.enqueue(new Callback<ResponseBody>() {
						@Override
						public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
							try {
								System.out.println(response.body().string());
							} catch (Exception e) {
								e.printStackTrace();
							}
						}

						@Override
						public void onFailure(Call<ResponseBody> call, Throwable t) {
							t.printStackTrace();
						}
					});

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(Call<ResponseBody> call, Throwable t) {
				t.printStackTrace();
			}
		});
	}

	static class LoginInfo {
		public String username;
		public String password;
	}

}

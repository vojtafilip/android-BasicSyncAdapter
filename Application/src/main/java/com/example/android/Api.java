package com.example.android;

import com.example.android.data.model.CalendarsEventsList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.mock.BehaviorDelegate;
import retrofit2.mock.MockRetrofit;
import retrofit2.mock.NetworkBehavior;

public class Api {

    private static final String TAG = "Api";

    public interface TeamZeusService {
        @GET("api/auth.test")
        Call<JsonObject> authTest(@Query("token") String token);

        @GET("api/users.list")
        Call<JsonObject> usersList(@Query("token") String token);

        @GET("api/inbox.list")
        Call<JsonObject> inboxList(@Query("token") String token, @Query("filter") String filter, @Query("take") int take);

        @GET("api/inbox.markAllAsRead")
        Call<JsonObject> inboxMarkAllAsRead(@Query("token") String token);

        @GET("api/groups.list")
        Call<JsonObject> groupsList(@Query("token") String token);

        @GET("api/locations.list")
        Call<JsonObject> locationsList(@Query("token") String token);

        @GET("api/chats.list")
        Call<JsonObject> chatsList(@Query("token") String token);

        @GET("api/chats.history")
        Call<JsonObject> chatsHistory(
                @Query("token") String token,
                @Query("user") String userId,
                @Query("message") String item,
                @Query("takeBefore") int takeBefore,
                @Query("takeAfter") int taketAfter
        );

        @GET("api/messages.star")
        Call<JsonObject> messagesStar(@Query("token") String token, @Query("message") String messageId);

        @GET("api/messages.unstar")
        Call<JsonObject> messagesUnstar(@Query("token") String token, @Query("message") String messageId);

        @GET("api/messages.send")
        Call<JsonObject> messagesSend(@Query("token") String token, @Query("user") String userId, @Query("text") String text);

        @GET("api/todos.list")
        Call<JsonObject> todosList(@Query("token") String token);

        @GET("api/todos.done")
        Call<JsonObject> todosDone(@Query("token") String token, @Query("todo") String todoId);

        @GET("api/todos.undone")
        Call<JsonObject> todosUndone(@Query("token") String token, @Query("todo") String todoId);

        @GET("api/todos.create")
        Call<JsonObject> todosCreate(
                @Query("token") String token,
                @Query("name") String name,
                @Query("note") String note,
                @Query("dueDate") String dueDate
        );

        @GET("api/todos.update")
        Call<JsonObject> todosUpdate(
                @Query("token") String token,
                @Query("todo") String id,
                @Query("name") String name,
                @Query("note") String note,
                @Query("dueDate") String dueDate,
                @Query("isDone") boolean isDone
        );

        @GET("api/todos.move")
        Call<JsonObject> todosMove(
                @Query("token") String token,
                @Query("todo") String id,
                @Query("prevItemId") String name
        );

        @GET("api/todos.delete")
        Call<JsonObject> todosDelete(@Query("token") String token, @Query("todo") String id);


//        @GET("api/calendars.events.list")
        @GET("desktop/calendar/events")
        Call<CalendarsEventsList> calendarsEventsList(@Query("appKey") String token, @Query("from") String from, @Query("to") String to);
//        Call<CalendarsEventsList> calendarsEventsList(@Query("token") String token, @Query("from") String from, @Query("to") String to);

    }


    private static TeamZeusService getRealService(Retrofit retrofit) {
        return retrofit.create(TeamZeusService.class);
    }


    public static TeamZeusService getService() {

        // prepare logging.
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
//        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(loggingInterceptor)
                .build();
        // TODO https://trello.com/c/aUrnoZRj/44-nastavit-spravne-logovani-api-zakazat-v-produkcni-verzi

        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl("https://tz4dev.teamzeus.cz")
                .baseUrl("https://codescale.teamzeusapp.com")
//                .addConverterFactory(ApiConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(createGson()))
                .client(client)
                .build();

//        return getMockService(retrofit);
        return getRealService(retrofit);

    }

    private static Gson createGson() {
        return new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();
        // XXX date format do nejake nasi Utils... + review, zda spravne format vcetne timezon
    }

    public static boolean responseStatus(JsonObject data) {
        return data.get("ok").getAsBoolean();
        // TODO https://trello.com/c/1rKezu6r/26-error-handling
    }

    public static <T> T responseData(JsonObject data, String member, TypeToken<T> typeToken) {
        Gson gson = createGson(); // TODO global?
        return gson.fromJson(data.get(member), typeToken.getType());
    }

    public static String responseError(JsonObject data) {
        return data.get("error").getAsString();
    }

//    public static <T> void loadApiData(Call<JsonObject> call, final String jsonElement, final TypeToken<? extends T> tokenType, final LoadDataCallback<T, Void> callback) {
//        loadApiData(call, jsonElement, tokenType, null, null, callback);
//    }
//
//    public static <T, E> void loadApiData(Call<JsonObject> call,
//                                       final String jsonElement,
//                                       final TypeToken<? extends T> tokenType,
//                                       final String extJsonElement,
//                                       final TypeToken<? extends E> extTokenType,
//                                       final LoadDataCallback<T, E> callback) {
//
//        final List<String> pathSegments = call.request().url().pathSegments();
//        final String apiPath = pathSegments.get(pathSegments.size() - 1);
//        L.d(TAG, "call api path: " + apiPath);
//
//        call.enqueue(new Callback<JsonObject>() {
//            @Override
//            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
//                L.d(TAG, "onResponse " + this);
//
//                ApiResult<T, E> result;
//
//                if(response.isSuccessful()) {
//                    JsonObject data = response.body();
//                    if (Api.responseStatus(data)) {
//                        T resultData = jsonElement != null ? Api.responseData(data, jsonElement, tokenType) : null;
//                        E extData = extJsonElement != null ? Api.responseData(data, extJsonElement, extTokenType) : null;
//                        result = new ApiResult<T, E>(resultData, extData);
//                    } else {
//                        // error
//                        // TODO https://trello.com/c/1rKezu6r/26-error-handling
//                        String error = Api.responseError(data);
//                        result = new ApiResult<T, E>(new ApiResult.ApiError(apiPath, error));
//                    }
//                } else {
//                    result = new ApiResult<T, E>(new ApiResult.ApiError(apiPath, new IOException("Response error (for " + apiPath + "): " + response.message())));
//                    // TODO https://trello.com/c/1rKezu6r/26-error-handling ... mozna nejakou nasi vnitrni exception.
//                }
//
//                callback.onLoadDone(result);
//            }
//
//            @Override
//            public void onFailure(Call<JsonObject> call, Throwable t) {
//                L.d(TAG, "onFailure " + this);
//                // TODO https://trello.com/c/1rKezu6r/26-error-handling
//
//                ApiResult<T, E> result = new ApiResult<T, E>(new ApiResult.ApiError(apiPath, t));
//
//                callback.onLoadDone(result);
//            }
//        });
//
//    }
//
//    public interface LoadDataCallback<T, E> {
//        void onLoadDone(ApiResult<T, E> result);
//    }
}

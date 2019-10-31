package com.sannmizu.nearby_alumni.denglu;

import com.sannmizu.nearby_alumni.NetUtils.Net;
import com.sannmizu.nearby_alumni.NetUtils.infoResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class dataget{
    public static void getdata(int userid, Databack databack){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Net.BaseHost)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        infoResponse.infoService service=retrofit.create(infoResponse.infoService.class);
        retrofit2.Call<infoResponse>call=service.info(userid);
        call.enqueue(new Callback<infoResponse>() {
            @Override
            public void onResponse(Call<infoResponse> call, Response<infoResponse> response) {
                if (response.body().getCode()==0) {
                    String name,sign,sex,constellation,career,icon,id,age,areaId;
                    id= String.valueOf(response.body().getData().getId());
                    name = response.body().getData().getInfo().getNickname();
                    age = String.valueOf(response.body().getData().getInfo().getAge());
                    sign = response.body().getData().getInfo().getSign();
                    sex = response.body().getData().getInfo().getSex();
                    constellation = response.body().getData().getInfo().getConstellation();
                    career = response.body().getData().getInfo().getCareer();
                    areaId = String.valueOf(response.body().getData().getInfo().getArea_id());
                    icon = response.body().getData().getInfo().getIcon_base64();
                    databack.ongetdata(id,name,age,sign,sex,constellation,career,areaId,icon);
                }
                else {

                }
            }

            @Override
            public void onFailure(Call<infoResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}

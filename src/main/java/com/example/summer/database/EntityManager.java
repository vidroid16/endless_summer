package com.example.summer.database;

import com.yandex.ydb.auth.iam.CloudAuthProvider;
import com.yandex.ydb.core.grpc.GrpcTransport;
import com.yandex.ydb.table.TableClient;
import com.yandex.ydb.table.query.DataQueryResult;
import com.yandex.ydb.table.query.Params;
import com.yandex.ydb.table.rpc.grpc.GrpcTableRpc;
import com.yandex.ydb.table.transaction.TxControl;
import lombok.NoArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import yandex.cloud.sdk.auth.provider.ApiKeyCredentialProvider;
import yandex.cloud.sdk.auth.provider.ComputeEngineCredentialProvider;

import java.nio.file.Paths;
import java.util.function.Consumer;

@NoArgsConstructor
public class EntityManager {

    public void execute(String query, Params params, Consumer<DataQueryResult> callback) {

        ClassPathResource cpr = new ClassPathResource("authorized_key.json");

        var authProvider = CloudAuthProvider.newAuthProvider(ApiKeyCredentialProvider.builder()
                .fromJson("{\n" +
                        "  \"id\": \"ajehvp9tpmp57ge8l89u\",\n" +
                        "  \"service_account_id\": \"ajerbq9dh7rl8goonsi9\",\n" +
                        "  \"created_at\": \"2022-11-10T15:04:29.410176414Z\",\n" +
                        "  \"key_algorithm\": \"RSA_2048\",\n" +
                        "  \"public_key\": \"-----BEGIN PUBLIC KEY-----\\nMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAl377j/RjT896jgnEHJC+\\n1qR+RkfRpvmpqNupRtcWPvOq2Nt/nFR5edQqcLg6dPUADmYeg5P0Lwe1Flvn61Yh\\nX/de5F71NxEngyAQoYDSucAo4nnF2+Uhukll54TI7jK6rLdBBFXZ4fp8s297jAOH\\ncYdaeyu3D5ZUpCQpjI595E664tX7xDZRDgdugJsgHMMPnIBK6shLWAWcXjQpvBI3\\nATASJYBnm3G5wpHLUIRQdE4NsSC123j9MRmv0OhLA3NSySewSaJnECllG6j4TJF/\\nvI6pTQGboI8KuzZkDiv82o/KhMc0XUIzh65nJjInzY5KOiMLh3B2uTxbLF6EvBl0\\nGQIDAQAB\\n-----END PUBLIC KEY-----\\n\",\n" +
                        "  \"private_key\": \"-----BEGIN PRIVATE KEY-----\\nMIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCXfvuP9GNPz3qO\\nCcQckL7WpH5GR9Gm+amo26lG1xY+86rY23+cVHl51CpwuDp09QAOZh6Dk/QvB7UW\\nW+frViFf917kXvU3ESeDIBChgNK5wCjiecXb5SG6SWXnhMjuMrqst0EEVdnh+nyz\\nb3uMA4dxh1p7K7cPllSkJCmMjn3kTrri1fvENlEOB26AmyAcww+cgErqyEtYBZxe\\nNCm8EjcBMBIlgGebcbnCkctQhFB0Tg2xILXbeP0xGa/Q6EsDc1LJJ7BJomcQKWUb\\nqPhMkX+8jqlNAZugjwq7NmQOK/zaj8qExzRdQjOHrmcmMifNjko6IwuHcHa5PFss\\nXoS8GXQZAgMBAAECggEACYhFlJRAX3g4R5GuaPcOmvuKBSq7CAeeOPAWtvdiVfRd\\nDqduYHbMQfHV+HZkbzwvPw4GjimUFsAkZn4X71iMYbkd8RQB1++DY0ke4sVJUm4Y\\njyTcYe6z8EByt15FNP+00UNNRamP5nEtbC5FQX/DWPhhpW27EgcCgmV9cR5SJjhZ\\nLy9K1tGT2elF6jnt283RRh5EHugzSbmTkNKxnpaW/Ag7Mhm28A43OhIFsxA0iSVx\\no3+My6oZKRuhMKQzb9Upenj9AOMWVWSMVzPlieZOSP52Z4iJVJC3m2u24C18rK/y\\nJlbNn9NCpHF+dkIrm2zxeI8BnH+uSbhK7srQRtghdQKBgQDCxEsMkG/8vGWL7u+r\\nuZ07b6QGaoQePpjl/anUEG4mv6lddLEoAL1Z2n7HSqpio+AtnDxmKenaxVZnKPn5\\nPUFOw9jpEkaemEvugkghNFjOgWmcKY7Tq7h+F3hL56qB41UjKkYtW1yQq2NfwJfM\\nYvgUof1yV6S7Avy1djASURO01wKBgQDHIBCs5Kh35vMJBy1s9EVnAAEw1Nd190no\\nk1j+kxCLgnL9zRUg05ozUJpIeMoKXsLAwVHNqaPcDNAoS40jO3hQJ4GCisTEu/mU\\n6PNzDc0y0PC75VR5NgpgQ5ncuaflkXC8cXoOUIopjXZ9wjq9kf6YFc9WS2IF6jHd\\nw+V03EYQjwKBgEOketAlRc+H+tiLpk+Eyne/AT99NN9wLBgMbjQJdMgeXornoTNc\\ndmbDfK5oXx6c/Zm5njx5KA/j42s2jwKh5JGfcTEZOM+R4yU1uClYDdPCnFQxFrqq\\n9AtOyWfLUKuFsabh4reT+GUCMSQrNk00viYkUGqqx7pvgibfojhqUibHAoGBAK72\\n1NAmBoV8b5ZCMT7oD8dmMKWojz1/MPjj57GgpUwALmwl9GeopnhqXXZKdDTSeMuH\\n0golAe7Lb2fIotYPXjMH03tR1X2MMcwPOEKjIOeCwdRHuIbc3hchryNMGmPT/LwH\\n9Zh4Wj4Sp8fuj1Cc5JjbqRgJGrNZycEzbP3WwtWvAoGAMtD4Xx0vOPYJYTPv0qx9\\nHPg241q31NCwiVrcoKGsGphZpu6sAo6mKXeRsgYBSnqNhwsAUNODriept7pmDW6f\\nrRlx/0GoRG6Jp9W79Fc7r7EZsrPUA9+LP6RInOUlzyNp34Vmh77nRRq7B+VNQYo1\\ngZy6dvo2xreS4A+fy/cnl9U=\\n-----END PRIVATE KEY-----\\n\"\n" +
                        "}")
                .build());

        GrpcTransport transport = GrpcTransport.forEndpoint(
                "ydb.serverless.yandexcloud.net:2135",
                "/ru-central1/b1g2r9itt925gce1e2je/etnrflq3ol8cgs7rqdbk")
                .withAuthProvider(authProvider)
                .withSecureConnection()
                .build();

        var tableClient = TableClient.newClient(GrpcTableRpc.useTransport(transport)).build();

        var session = tableClient.createSession()
                .join()
                .expect("Error: can't create session");

        var preparedQuery = session.prepareDataQuery(query)
                .join()
                .expect("Error: query preparation failed");

        var result = preparedQuery.execute(TxControl.serializableRw().setCommitTx(true), params)
                .join()
                .expect("Error: query execution failed");

        if (callback != null) {
            callback.accept(result);
        }

    }

    public void execute(String query, Params params) {
        execute(query, params, null);
    }

}
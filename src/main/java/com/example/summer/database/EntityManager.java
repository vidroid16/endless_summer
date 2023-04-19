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
                .fromJson("")
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

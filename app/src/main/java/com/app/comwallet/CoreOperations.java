package com.app.comwallet;

import java.net.URISyntaxException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import io.emeraldpay.polkaj.api.PolkadotApi;
import io.emeraldpay.polkaj.api.PolkadotMethod;
import io.emeraldpay.polkaj.api.PolkadotSubscriptionApi;
import io.emeraldpay.polkaj.api.RpcCall;
import io.emeraldpay.polkaj.api.StandardCommands;
import io.emeraldpay.polkaj.api.Subscription;
import io.emeraldpay.polkaj.apihttp.JavaHttpAdapter;
import io.emeraldpay.polkaj.apiws.JavaHttpSubscriptionAdapter;
import io.emeraldpay.polkaj.json.BlockJson;
import io.emeraldpay.polkaj.json.BlockResponseJson;
import io.emeraldpay.polkaj.json.MethodsJson;
import io.emeraldpay.polkaj.json.RuntimeVersionJson;
import io.emeraldpay.polkaj.json.SystemHealthJson;
import io.emeraldpay.polkaj.scaletypes.AccountInfo;
import io.emeraldpay.polkaj.tx.AccountRequests;
import io.emeraldpay.polkaj.types.Address;
import io.emeraldpay.polkaj.types.Hash256;

public class CoreOperations {

    public static String NODE_URL = "commune-api-node-2.communeai.net";

    public static void main(String[] args) throws Exception {

        //Create RPC Adapter

        JavaHttpAdapter httpAdapter = JavaHttpAdapter.newBuilder().connectTo("https://" + NODE_URL).build();

        PolkadotApi api = PolkadotApi.newBuilder()
                .rpcCallAdapter(httpAdapter)
                .build();

        //Acc address for querying
        Address address = Address.from("5GZBhMZZRMWCiqgqdDGZCGo16Kg5aUQUcpuUGWwSgHn9HbRC");

        getSystemInfo(api);
        getAccBalance(address,api);
        getRPCMethods(api);
        subscribeNewHeaders();

    }

    public static void getSystemInfo(PolkadotApi api) throws ExecutionException, InterruptedException, TimeoutException {
        System.out.println("** --- SYSTEM INFO --- **\n");
        Future<Hash256> hashFuture = api.execute(
                // use RpcCall.create to define the request
                // the first parameter is Class / JavaType of the expected result
                // second is the method name
                // and optionally a list of parameters for the call
                RpcCall.create(Hash256.class, PolkadotMethod.CHAIN_GET_FINALIZED_HEAD)
        );

        Hash256 hash = hashFuture.get();
        Hash256 blockHash = api.execute(PolkadotApi.commands().getBlockHash()).get();

        Future<BlockResponseJson> blockFuture = api.execute(
                // Another way to prepare a call, instead of manually constructing RpcCall instances
                // is to use standard commands provided by PolkadotApi.commands()
                // the following line is same as calling with
                // RpcCall.create(BlockResponseJson.class, "chain_getBlock", hash)
                PolkadotApi.commands().getBlock(hash)
        );
        BlockResponseJson block = blockFuture.get();

        String version = api.execute(PolkadotApi.commands().systemVersion())
                .get(5, TimeUnit.SECONDS);

        RuntimeVersionJson runtimeVersion = api.execute(PolkadotApi.commands().getRuntimeVersion())
                .get(5, TimeUnit.SECONDS);

        SystemHealthJson health = api.execute(PolkadotApi.commands().systemHealth())
                .get(5, TimeUnit.SECONDS);

        System.out.println("Software: " + version);
        System.out.println("Spec: " + runtimeVersion.getSpecName() + "/" + runtimeVersion.getSpecVersion());
        System.out.println("Impl: " + runtimeVersion.getImplName() + "/" + runtimeVersion.getImplVersion());
        System.out.println("Peers count: " + health.getPeers());
        System.out.println("Is syncing: " + health.getSyncing());
        System.out.println("Current head: " + hash);
        System.out.println("Current block hash: " + blockHash);
        System.out.println("Current height: " + block.getBlock().getHeader().getNumber());
        System.out.println("State hash: " + block.getBlock().getHeader().getStateRoot());

    }

    public static void getAccBalance(Address address, PolkadotApi api) throws ExecutionException, InterruptedException {
        System.out.println("** --- BALANCE --- **\n");
        AccountInfo accountInfo = AccountRequests.balanceOf(address).execute(api).get();
        System.out.println("Balance: " + accountInfo.getData().getFree());
    }

    public static void getRPCMethods(PolkadotApi api) throws ExecutionException, InterruptedException {
        System.out.println("** --- RPC METHODS --- **\n");
        MethodsJson methodsJson = api.execute(StandardCommands.getInstance().methods()).get();
        System.out.println(methodsJson.getMethods().toString());
    }

    public static void subscribeNewHeaders() throws URISyntaxException, ExecutionException, InterruptedException, TimeoutException {
        JavaHttpSubscriptionAdapter wsAdapter = JavaHttpSubscriptionAdapter.newBuilder().connectTo("wss://" + NODE_URL).build();
        PolkadotApi api = PolkadotApi.newBuilder()
                .subscriptionAdapter(wsAdapter)
                .build();

        wsAdapter.connect().get(5, TimeUnit.SECONDS);
        System.out.println("** --- Successfully connected to Commune WSS --- **\n");
        System.out.println("** --- Subscribed new block headers --- **\n");

        Future<Subscription<BlockJson.Header>> hashFuture = api.subscribe(
                PolkadotSubscriptionApi.subscriptions().newHeads()
        );

        Subscription<BlockJson.Header> subscription = hashFuture.get();

        subscription.handler((Subscription.Event<BlockJson.Header> event) -> {
            BlockJson.Header header = event.getResult();
            List<String> line = List.of(
                    Instant.now().truncatedTo(ChronoUnit.SECONDS).toString(),
                    header.getNumber().toString(),
                    header.getStateRoot().toString());
            System.out.println(String.join("\t", line));
        });

    }

}


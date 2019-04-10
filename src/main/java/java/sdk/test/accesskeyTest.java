package java.sdk.test;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.util.Arrays.asList;

public class accesskeyTest {
    String accessKeyId = "pUXfTWOCl9g7AgW12g1P";
    String accessKeySecret = "JtpaQsTuqyxGChG524k3NTwKI8ug2lDFXpzsnP9x";
    String instanceOfferingUuid = "8f47ae12042c402da01050d36931a401";
    String imageUuid="b6fda2461274500c9e928ebe39d5e8a1";
    String l3NetworkUuid = "e11c312533894bd380f246fb84931e91";
    String pubL3Uuid="a60afdc8e310414ea89bccaa01ab6531";

    private VmInstanceInventory createVm(String name) {
        CreateVmInstanceAction action = new CreateVmInstanceAction();
        action.name = name;
        action.instanceOfferingUuid = instanceOfferingUuid;
        action.imageUuid = imageUuid;
        action.l3NetworkUuids = asList(l3NetworkUuid);
        action.accessKeyId = accessKeyId;
        action.accessKeySecret = accessKeySecret;

        CreateVmInstanceAction.Result result = action.call();
        return result.value.inventory;
    }

    private List<VmInstanceInventory> queryVms() {
        QueryVmInstanceAction action = new QueryVmInstanceAction();
        action.conditions = asList("name~=%vyos-test%");
        action.accessKeyId = accessKeyId;
        action.accessKeySecret = accessKeySecret;

        QueryVmInstanceAction.Result result = action.call();
        return result.value.inventories;
    }

    private VipInventory createVip(String name) {
        CreateVipAction action = new CreateVipAction();
        action.name = name;
        action.l3NetworkUuid = pubL3Uuid;
        action.accessKeyId = accessKeyId;
        action.accessKeySecret = accessKeySecret;
        //action.sessionId = sessionUUid;

        CreateVipAction.Result result = action.call();
        return result.value.inventory;
    }

    private EipInventory createEip(String name, VipInventory vip, VmInstanceInventory vm) {
        VmNicInventory nic = (VmNicInventory)vm.getVmNics().get(0);
        CreateEipAction action = new CreateEipAction();
        action.name = name;
        action.vipUuid = vip.uuid;
        action.vmNicUuid = nic.uuid;
        action.accessKeyId = accessKeyId;
        action.accessKeySecret = accessKeySecret;

        CreateEipAction.Result result = action.call();
        return result.value.inventory;
    }

    private SessionInventory createSession() {
        LogInByAccountAction accountAction = new LogInByAccountAction();
        accountAction.accountName = "admin";
        accountAction.password = "b109f3bbbc244eb82441917ed06d618b9008dd09b3befd1b5e07394c706a8bb980b1d7785e5976ec049b46df5f1326af5a2ea6d103fd07c95385ffab0cacbc86";

        LogInByAccountAction.Result result = accountAction.call();
        return result.value.inventory;
    }

    public static void main(String[] args) {
        ZSClient.configure(
                new ZSConfig.Builder().setHostname("10.86.4.243").setPort(8080).setContextPath("zstack")
                        .setDefaultPollingInterval(100, TimeUnit.MILLISECONDS)
                        .setDefaultPollingTimeout(TimeUnit.MINUTES.toMillis(15), TimeUnit.MILLISECONDS)
                        .setReadTimeout(10, TimeUnit.MINUTES)
                        .setWriteTimeout(10, TimeUnit.MINUTES)
                        .build()
        );


        final VmAPITest test = new VmAPITest();
        test.createVm("shixin-1");

        /*
        SessionInventory session = test.createSession();
        test.sessionUUid = session.uuid;
        List<VmInstanceInventory> vms = test.queryVms();
        Collections.sort(vms, new Comparator<VmInstanceInventory>() {
            public int compare(VmInstanceInventory vm1, VmInstanceInventory vm2) {
                return vm1.name.compareTo(vm2.name);
            }
        });

        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(100);

        System.out.println(String.format("get %d vms with vyos-test", vms.size()));
        for (VmInstanceInventory vm : vms) {
            final String name = vm.name;
            final VmInstanceInventory vmInv = vm;
            fixedThreadPool.execute(new Runnable() {
                public void run() {
                    System.out.println("create eip for vm: " + name);
                    VipInventory vip = test.createVip("vip-" + name);
                    EipInventory eip = test.createEip("eip-" + name, vip, vmInv);
                    System.out.println("create eip successfully for vm: " + name);
                }
            });

        }*/
    }
}

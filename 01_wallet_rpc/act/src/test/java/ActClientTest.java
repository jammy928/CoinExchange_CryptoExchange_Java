import com.bizzan.bc.wallet.component.ActClient;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.net.MalformedURLException;
import java.net.URISyntaxException;

public class ActClientTest {

    public static void main(String[] args) throws MalformedURLException, URISyntaxException, UnirestException {
        ActClient client = new ActClient("http://act:123456@47.74.42.87:8900/rpc");
        //System.out.println(client.getBlock(2000L));
        System.out.println(client.getAddressBalance("ACTKBscoFpQzHCxgtpYUhqo97mDAdEVmBqEp"));
    }
}

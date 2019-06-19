package org.perfectbroken.geralt.client;

import org.apache.http.HttpClientConnection;
import org.apache.http.HttpHost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ConnectionRequest;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.ManagedHttpClientConnection;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.conn.routing.RouteInfo;
import org.apache.http.conn.routing.RouteTracker;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import java.util.concurrent.TimeUnit;

public class ComponentLearn {


    public static void main(String[] args) throws Exception{

        /**
         * {@link RouteInfo} 接口表示到目标主机的确定路由信息.
         * {@link HttpRoute} 是RouteInfo的具体实现.但是策略是固定死的
         * {@link RouteTracker} 是可RouteInfo变的实现.
         */
        RouteInfo routeInfo;
        HttpRoute httpRoute;
        RouteTracker routeTracker;

        /**
         * {@link HttpRoutePlanner} 接口表示到给定目标的完整路由策略。
         * SystemDefaultRoutePlanner是基于java.net.ProxySelector.
         */
        HttpRoutePlanner httpRoutePlanner;

        /**
         * 由于Http连接具有复杂,状态机,线程不安全的特性.
         * Http client使用接口{@link HttpClientConnectionManager}管理连接
         * 其用处有:
         * 1.用于创建Http连接的工厂
         * 2.管理连接的生命周期
         * 3.保证单一线程安全访问一条连接
         *
         * {@link HttpClientConnectionManager} 通过{@link ManagedHttpClientConnection}接口的实例管理连接.
         * {@link ManagedHttpClientConnection} 的实例为一个真实连接的代理类.
         * 当连接释放或调用方取消时 该连接将与代理类断开并返回到manager
         * 尽管断开后代理类的实例仍然存在,但他已不能执行任何IO操作
         */
        HttpClientConnectionManager httpClientConnectionManager;
        ManagedHttpClientConnection managedHttpClientConnection;

        //============================apache 官方demo================================
        HttpClientContext context = HttpClientContext.create();
        HttpClientConnectionManager connMrg = new BasicHttpClientConnectionManager();
        HttpRoute route = new HttpRoute(new HttpHost("localhost", 80));
        // Request new connection. This can be a long process
        ConnectionRequest connRequest = connMrg.requestConnection(route, null);
        // Wait for connection up to 10 sec
        HttpClientConnection conn = connRequest.get(10, TimeUnit.SECONDS);
        try {
            // If not open
            if (!conn.isOpen()) {
                // establish connection based on its route info
                connMrg.connect(conn, route, 1000, context);
                // and mark it as route complete
                connMrg.routeComplete(conn, route, context);
            }
            // Do useful things with the connection.
        } finally {
            connMrg.releaseConnection(conn, null, 1, TimeUnit.MINUTES);
        }
        //============================apache 官方demo================================

        /**
         * {@link BasicHttpClientConnectionManager} 是HttpClientConnectionManager的一个简单实现
         * 同一时间只能保持一条连接,好了你可以去死了 狗带狗带
         */
        BasicHttpClientConnectionManager basicHttpClientConnectionManager;

        /**
         * {@link PoolingHttpClientConnectionManager} 是更全面的实现
         * 可以同时为多条线程提供服务
         * 连接池中将对连接进行持久化 当请求时将根据路由租用连接而不是创建一个全新的连接
         * 连接池对于同一个路由将设定最大连接数限制 TODO 策略在哪?
         * 默认同一个路由连接最多2条,总连接数最多20条
         * 如若调整,请见下方demo
         */
        PoolingHttpClientConnectionManager poolingHttpClientConnectionManager;
        //============================apache 官方demo================================
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        // Increase max total connection to 200
        cm.setMaxTotal(200);
        // Increase default max connection per route to 20
        cm.setDefaultMaxPerRoute(20);
        // Increase max connections for localhost:80 to 50
        HttpHost localhost = new HttpHost("locahost", 80);
        cm.setMaxPerRoute(new HttpRoute(localhost), 50);

        CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(cm)
                .build();
        //============================apache 官方demo================================

        /**
         * 当不在需要Connection实例,手动关闭释放系统资源,见下方demo
         */
        //============================apache 官方demo================================
        httpClient.close();
        //============================apache 官方demo================================

        /**
         * 当多条线程同时访问时
         * {@link PoolingHttpClientConnectionManager} 将根据它的配置对连接对象进行分配。
         */
        //============================apache 官方demo================================
        //============================apache 官方demo================================
        //============================apache 官方demo================================
        //============================apache 官方demo================================
        //============================apache 官方demo================================
        //============================apache 官方demo================================
    }
}

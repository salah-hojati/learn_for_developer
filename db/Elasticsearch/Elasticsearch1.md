Dev Tools is not a separate package you install. It is a built-in console feature of **Kibana**, so installing Kibana on your Ubuntu machine is all you need to get access to it.

### 📦 How to Install Kibana on Ubuntu

The most reliable way to install Kibana is using the official Elastic APT repository, which ensures you get the correct version and can easily update it later.

**1. Add the Elastic APT Repository**
First, import the GPG key and add the repository to your system's sources. Make sure the version number (`8.x` in the example) matches your Elasticsearch installation.

```bash
wget -qO - https://artifacts.elastic.co/GPG-KEY-elasticsearch | sudo apt-key add -
echo "deb https://artifacts.elastic.co/packages/8.x/apt stable main" | sudo tee /etc/apt/sources.list.d/elastic-8.x.list
sudo apt update
```


**2. Install Kibana**
Now install the Kibana package.

```bash
sudo apt install -y kibana
```


**3. Configure Kibana (Essential)**
You must edit the configuration file to tell Kibana where your Elasticsearch instance is located. Open `/etc/kibana/kibana.yml` with a text editor and ensure the `elasticsearch.hosts` line points to your Elasticsearch server (usually `http://localhost:9200`).

```yaml
# /etc/kibana/kibana.yml
elasticsearch.hosts: ["http://localhost:9200"]
```
If you need to access Kibana from another computer, also set `server.host` to `0.0.0.0`.

**4. Start and Enable Kibana**
Start the Kibana service and set it to launch automatically on boot.

```bash
sudo systemctl enable --now kibana
```


**5. Verify the Installation**
Check that the service is running correctly.

```bash
sudo systemctl status kibana
```
You can also test with `curl http://localhost:5601`. A successful response will return HTML content.

**6. Access the Kibana UI**
Open your web browser and go to `http://localhost:5601` (or `http://your_server_ip:5601` if accessing remotely). If you have a firewall enabled, make sure to allow port `5601`.

---

### 🛠️ How to Use Kibana Dev Tools (Console)

Once Kibana is running, you can open Dev Tools to interact directly with your Elasticsearch cluster.

**1. Open the Console**
In the Kibana interface, look for **"Dev Tools"** in the left-hand navigation menu or use the global search bar to find "Console".

**2. Understand the Interface**
You will see a split-pane editor. The **left side** is where you write requests, and the **right side** displays the responses from Elasticsearch.

**3. Write and Run a Request**
The Console uses a simplified syntax for HTTP requests. Here is a simple example to check your cluster's health:

**Left pane (Request):**
```json
GET /_cluster/health
```


Click the green **"Play"** button (▶) next to the request, or press `Ctrl/Cmd + Enter` to execute it. The result will instantly appear in the right pane.

**4. Try a More Advanced Query**
You can also send full search queries with request bodies:

```json
GET /_search
{
  "query": {
    "match_all": {}
  }
}
```


**Key Features to Know:**
*   **Autocomplete**: The Console provides context-aware suggestions as you type, showing you available parameters and APIs.
*   **Comments**: You can add comments with `#` or `//` to document your queries or temporarily disable parts of a request.
*   **Variables**: Use the **"Variables"** button to define reusable values that you can insert into your requests.

In short, installing Kibana via the APT repository and configuring it to connect to Elasticsearch gives you the Dev Tools console, which is a powerful way to send requests and manage your cluster directly from a web interface.
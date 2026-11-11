Setting up PlantUML in IntelliJ IDEA on Ubuntu involves two main parts: installing the required system dependencies (Java and Graphviz) and then configuring the IntelliJ plugin. Here is a step-by-step guide to get you started.

### 🛠️ Step 1: Install System Prerequisites (Ubuntu)

PlantUML relies on Java to run and Graphviz to generate complex diagrams. Open your terminal and run the following commands:

1.  **Update your package list**:
    ```bash
    sudo apt update
    ```
2.  **Install Java (JRE/JDK)**. PlantUML requires Java 8 or higher.
    ```bash
    sudo apt install default-jre
    ```
    *Alternatively, you can install a specific JDK like `openjdk-11-jdk` or `openjdk-17-jdk` if you prefer.*
3.  **Install Graphviz**. This is essential for rendering many diagram types.
    ```bash
    sudo apt install graphviz
    ```

### 💡 Step 2: Install the IntelliJ IDEA Plugin

1.  Open IntelliJ IDEA.
2.  Go to **Settings** (or **Preferences** on macOS) -> **Plugins**.
3.  Select the **Marketplace** tab and search for `PlantUML integration`.
4.  Click **Install** and restart the IDE when prompted.

### ⚙️ Step 3: Configure the Plugin

After restarting, you need to tell the plugin where to find the Graphviz `dot` executable.

1.  Go to **Settings** -> **Other Settings** -> **PlantUML** (you can also search for "PlantUML" in the settings search bar).
2.  Locate the setting for the **`dot` executable path**.
3.  Set the path to the `dot` file. On Ubuntu, after installing Graphviz via `apt`, the executable is typically located at `/usr/bin/dot`. You can verify this by running `which dot` in your terminal.
4.  Apply the changes.

### ✅ Step 4: Verify and Start Using PlantUML

1.  In your IntelliJ project, right-click on a directory, go to **New**, and select **PlantUML File**.
2.  Give your file a name and choose a diagram type (e.g., Class, Sequence).
3.  A `.puml` file will be created with some sample code. You should see a live preview of your diagram in a side panel.
4.  Any changes you make to the text in the `.puml` file will be reflected in the preview automatically.

### 🔧 Troubleshooting: "Graphviz not found"

A common issue is the plugin failing to locate Graphviz, even after installation.

-   **Symptom**: The preview pane shows an error like "`graphviz not found`" or "`dot executable not found`".
-   **Solution**:
    1.  Double-check the path in **Settings** -> **Other Settings** -> **PlantUML**. Ensure it points directly to the `dot` executable (e.g., `/usr/bin/dot`).
    2.  As an alternative, you can set an environment variable named `GRAPHVIZ_DOT` that contains the full path to the executable. You can add this line to your `~/.bashrc` or `~/.profile` file:
        ```bash
        export GRAPHVIZ_DOT=/usr/bin/dot
        ```
        After adding the line, run `source ~/.bashrc` and restart IntelliJ IDEA.

### ✨ A Quick Tip for Better Diagrams

You can maintain consistent styling across your diagrams by defining colors in a separate `.puml` file and including it in your main diagram files. For example, create a `Style.puml` and use `!include Style.puml` in other files.

This setup will allow you to create and edit PlantUML diagrams directly within IntelliJ IDEA on your Ubuntu system. If you run into any specific errors during the process, feel free to ask.
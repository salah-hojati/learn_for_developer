import { useState } from "react";

function Toggle() {
    const [isVisible, setIsVisible] = useState(true);

    return (
        <div>
            <button onClick={() => setIsVisible(!isVisible)}>
                {isVisible ? "Hide" : "Show"}
            </button>
            {isVisible && <p>Now you see me!</p>}
        </div>
    );
}
export default Toggle;
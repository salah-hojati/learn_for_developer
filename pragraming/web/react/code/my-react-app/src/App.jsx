import Counter from "./Counter";
import Toggle from "./Toggle";
import UserCard from "./UserCard.jsx";

function App() {
  return (
      <div>
        <h1>Hello React</h1>
        <p>This is my first component.</p>
          <Greeting name="Alex" />
          <Greeting name="Sam" />
          <Greeting name="Taylor" />

          <UserCard name="Ali" role="manager" age={35} />
          <UserCard name="Sara" role="designer" age={17} />

          <Counter />
          <Toggle />


      </div>

  );
}
function Greeting({ name }) {
  return <h1>Hello, {name}!</h1>;
}


export default App;
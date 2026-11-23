import Counter from "./Counter.jsx"
import Toggle from "./Toggle.jsx"
function Greeting({name}){
  return <h1>Hello, {name}</h1>
}

function UserCard({ name, role, age }) {
  return (
      <div>
        <Counter/>
        <Toggle/>

      </div>
  );
}

function App() {
  return (
      <div>
       <Greeting name="ali"/>
       <UserCard name="sara" role="manager" age={35}/>
       <UserCard name="maryam" role="manager" age={35}/>
      </div>
  );
}

export default App;
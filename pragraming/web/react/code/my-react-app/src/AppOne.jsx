import UserCard from "./UserCard.jsx";


const users = [
    { id: 1, name: "Ali", role: "manager", age: 35 },
    { id: 2, name: "Sara", role: "designer", age: 17 },
    { id: 3, name: "Reza", role: "developer", age: 28 },
];

function AppOne() {
    return (
        <div>
            {users.map((user) => (
                <UserCard
                    key={user.id}
                    name={user.name}
                    role={user.role}
                    age={user.age}
                />
            ))}
        </div>
    );
}
export default AppOne;
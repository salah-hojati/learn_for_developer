

function UserCard({ name, role, age }) {
    return (
        <div>
            <h2>{name}</h2>
            <p>Role: {role}</p>
            <p>Age: {age}</p>
            <p>Status: {age >= 18 ? "Adult" : "Minor"}</p>
        </div>
    );
}
export default UserCard;
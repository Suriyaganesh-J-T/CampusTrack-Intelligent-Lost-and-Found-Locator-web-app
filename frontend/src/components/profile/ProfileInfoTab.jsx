export default function ProfileInfoTab({ user }) {
    return (
        <div className="space-y-4 text-gray-700">
            <p><strong>Name:</strong> {user.firstName} {user.lastName}</p>
            <p><strong>Email:</strong> {user.email}</p>
            <p><strong>Role:</strong> {user.role}</p>
            <p><strong>Phone:</strong> {user.phone || "Not added"}</p>
            <p><strong>Bio:</strong> {user.bio || "No bio available"}</p>
            <p><strong>Joined:</strong> {new Date(user.createdAt).toLocaleString()}</p>

            <div className="mt-4">
                <img
                    src={user.profileImage || "/default-avatar.png"}
                    alt="Profile"
                    className="w-28 h-28 rounded-full border"
                />
            </div>
        </div>
    );
}

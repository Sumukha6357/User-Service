"use client";

import { FormEvent, useCallback, useEffect, useMemo, useState } from "react";
import {
  createUser,
  deleteUser,
  getUserById,
  listUsers,
  login,
  updateUser,
  type User,
  type UserPayload,
} from "@/lib/api";

type Session = {
  token: string;
  role: "ADMIN" | "VIEWER";
  username: string;
};

const DEFAULT_FORM: UserPayload = { userName: "", age: 18, gender: "Male" };

export default function Home() {
  const [session, setSession] = useState<Session | null>(null);
  const [loginForm, setLoginForm] = useState({ username: "admin", password: "admin123" });
  const [status, setStatus] = useState<string>("Login with admin/admin123 or viewer/viewer123");

  const [items, setItems] = useState<User[]>([]);
  const [search, setSearch] = useState("");
  const [sortBy, setSortBy] = useState("userName");
  const [direction, setDirection] = useState<"ASC" | "DESC">("ASC");
  const [page, setPage] = useState(0);
  const [size, setSize] = useState(10);
  const [totalPages, setTotalPages] = useState(0);

  const [formMode, setFormMode] = useState<"create" | "update">("create");
  const [targetId, setTargetId] = useState("");
  const [userForm, setUserForm] = useState<UserPayload>(DEFAULT_FORM);
  const [busy, setBusy] = useState(false);

  const apiBase = useMemo(() => process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8080", []);

  const refreshUsers = useCallback(async (nextPage = page) => {
    if (!session) return;
    const response = await listUsers(session.token, { page: nextPage, size, search, sortBy, direction });
    if (!response.ok) {
      setStatus((response.error as { message?: string }).message || "Failed to fetch users");
      return;
    }
    setItems(response.data.data.items);
    setPage(response.data.data.page);
    setTotalPages(response.data.data.totalPages);
    setStatus(`Loaded ${response.data.data.totalElements} users`);
  }, [direction, page, search, session, size, sortBy]);

  useEffect(() => {
    if (session) {
      refreshUsers(0);
    }
  }, [session, refreshUsers]);

  async function handleLogin(event: FormEvent) {
    event.preventDefault();
    setBusy(true);
    const response = await login(loginForm);
    setBusy(false);

    if (!response.ok) {
      setStatus((response.error as { message?: string }).message || "Login failed");
      return;
    }

    setSession({
      token: response.data.data.accessToken,
      role: response.data.data.role,
      username: loginForm.username,
    });
    setStatus(`Logged in as ${loginForm.username} (${response.data.data.role})`);
  }

  async function handleSubmitUser(event: FormEvent) {
    event.preventDefault();
    if (!session) return;
    setBusy(true);

    try {
      if (formMode === "create") {
        const response = await createUser(session.token, userForm);
        if (!response.ok) {
          setStatus((response.error as { message?: string }).message || "Create failed");
        } else {
          setStatus(`Created user #${response.data.data.userId}`);
          await refreshUsers(0);
        }
      } else {
        const id = Number(targetId);
        const response = await updateUser(session.token, id, userForm);
        if (!response.ok) {
          setStatus((response.error as { message?: string }).message || "Update failed");
        } else {
          setStatus(`Updated user #${response.data.data.userId}`);
          await refreshUsers(page);
        }
      }
    } finally {
      setBusy(false);
    }
  }

  async function handleLoadUserForEdit() {
    if (!session) return;
    const id = Number(targetId);
    if (!id) {
      setStatus("Enter user ID");
      return;
    }
    setBusy(true);
    const response = await getUserById(session.token, id);
    setBusy(false);
    if (!response.ok) {
      setStatus((response.error as { message?: string }).message || "Fetch failed");
      return;
    }
    setUserForm({
      userName: response.data.data.userName,
      age: response.data.data.age,
      gender: response.data.data.gender,
    });
    setFormMode("update");
    setStatus(`Loaded user #${id} into update form`);
  }

  async function handleDelete(id: number) {
    if (!session) return;
    setBusy(true);
    const response = await deleteUser(session.token, id);
    setBusy(false);
    if (!response.ok) {
      setStatus((response.error as { message?: string }).message || "Delete failed");
      return;
    }
    setStatus(`Soft deleted user #${id}`);
    await refreshUsers(page);
  }

  return (
    <main className="page">
      <header className="hero">
        <p className="hero-kicker">User Service</p>
        <h1>Secure Admin Dashboard</h1>
        <p className="hero-subtitle">API: <code>{apiBase}</code></p>
      </header>

      {!session ? (
        <section className="card" style={{ maxWidth: 440 }}>
          <h2>Login</h2>
          <form className="form" onSubmit={handleLogin}>
            <label>Username
              <input value={loginForm.username} onChange={(e) => setLoginForm((v) => ({ ...v, username: e.target.value }))} />
            </label>
            <label>Password
              <input type="password" value={loginForm.password} onChange={(e) => setLoginForm((v) => ({ ...v, password: e.target.value }))} />
            </label>
            <button disabled={busy}>{busy ? "Signing in..." : "Sign in"}</button>
          </form>
        </section>
      ) : (
        <>
          <section className="card" style={{ marginBottom: "1rem" }}>
            <h2>Session</h2>
            <p>Logged in as <strong>{session.username}</strong> ({session.role})</p>
            <button onClick={() => setSession(null)}>Logout</button>
          </section>

          <section className="grid" style={{ gridTemplateColumns: "1.4fr 1fr" }}>
            <article className="card">
              <h2>Users</h2>
              <div className="form" style={{ gridTemplateColumns: "repeat(5, minmax(0, 1fr))", alignItems: "end" }}>
                <label>Search
                  <input value={search} onChange={(e) => setSearch(e.target.value)} />
                </label>
                <label>Sort by
                  <select value={sortBy} onChange={(e) => setSortBy(e.target.value)}>
                    <option value="userName">Name</option>
                    <option value="age">Age</option>
                    <option value="createdAt">Created</option>
                  </select>
                </label>
                <label>Direction
                  <select value={direction} onChange={(e) => setDirection(e.target.value as "ASC" | "DESC")}>
                    <option value="ASC">ASC</option>
                    <option value="DESC">DESC</option>
                  </select>
                </label>
                <label>Page size
                  <select value={size} onChange={(e) => setSize(Number(e.target.value))}>
                    <option value={5}>5</option>
                    <option value={10}>10</option>
                    <option value={20}>20</option>
                  </select>
                </label>
                <button onClick={() => refreshUsers(0)} disabled={busy}>Refresh</button>
              </div>

              <div style={{ overflowX: "auto", marginTop: "0.8rem" }}>
                <table className="table">
                  <thead><tr><th>ID</th><th>Name</th><th>Age</th><th>Gender</th><th>Actions</th></tr></thead>
                  <tbody>
                    {items.map((u) => (
                      <tr key={u.userId}>
                        <td>{u.userId}</td><td>{u.userName}</td><td>{u.age}</td><td>{u.gender}</td>
                        <td>
                          {session.role === "ADMIN" && (
                            <button className="mini danger" onClick={() => handleDelete(u.userId)} disabled={busy}>Delete</button>
                          )}
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>

              <div style={{ display: "flex", gap: "0.5rem", marginTop: "0.8rem" }}>
                <button disabled={page <= 0 || busy} onClick={() => refreshUsers(page - 1)}>Prev</button>
                <span style={{ alignSelf: "center" }}>Page {page + 1} / {Math.max(totalPages, 1)}</span>
                <button disabled={page + 1 >= totalPages || busy} onClick={() => refreshUsers(page + 1)}>Next</button>
              </div>
            </article>

            <article className="card">
              <h2>{formMode === "create" ? "Create User" : "Update User"}</h2>
              {session.role === "ADMIN" ? (
                <>
                  <div className="form">
                    <label>Target ID (for update)
                      <input value={targetId} onChange={(e) => setTargetId(e.target.value)} />
                    </label>
                    <div style={{ display: "flex", gap: "0.5rem" }}>
                      <button onClick={handleLoadUserForEdit} disabled={busy}>Load for Update</button>
                      <button onClick={() => { setFormMode("create"); setUserForm(DEFAULT_FORM); }} disabled={busy}>New</button>
                    </div>
                  </div>
                  <form className="form" onSubmit={handleSubmitUser} style={{ marginTop: "0.8rem" }}>
                    <label>Name
                      <input value={userForm.userName} onChange={(e) => setUserForm((v) => ({ ...v, userName: e.target.value }))} required />
                    </label>
                    <label>Age
                      <input type="number" min={1} max={120} value={userForm.age} onChange={(e) => setUserForm((v) => ({ ...v, age: Number(e.target.value) }))} required />
                    </label>
                    <label>Gender
                      <select value={userForm.gender} onChange={(e) => setUserForm((v) => ({ ...v, gender: e.target.value }))}>
                        <option>Male</option><option>Female</option><option>Other</option>
                      </select>
                    </label>
                    <button disabled={busy}>{formMode === "create" ? "Create" : "Update"}</button>
                  </form>
                </>
              ) : (
                <p>Viewer role has read-only access.</p>
              )}
            </article>
          </section>
        </>
      )}

      <section className="result-panel"><h2>Status</h2><p>{status}</p></section>
    </main>
  );
}

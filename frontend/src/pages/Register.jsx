import React from "react";
import { useNavigate } from "react-router-dom";
import api from "../services/api";
import { useFormik } from "formik";
import * as yup from "yup";

const schema = yup.object({
    name: yup.string().required("Name is required"),
    email: yup.string().email("Invalid email").required("Email is required"),
    password: yup.string().min(6, "Minimum 6 characters").required("Password required"),
    role: yup.string().oneOf(["STUDENT", "ADMIN"]).required("Role required"),
});

export default function Register() {
    const navigate = useNavigate();

    const formik = useFormik({
        initialValues: { name: "", email: "", password: "", role: "STUDENT" },
        validationSchema: schema,
        onSubmit: async (values, { setSubmitting }) => {
            try {
                const res = await api.post("/auth/register", values);
                localStorage.setItem("jwt", res.data.token);
                localStorage.setItem("role", res.data.role);
                navigate("/");
            } catch (err) {
                alert(err?.response?.data || err.message);
            } finally {
                setSubmitting(false);
            }
        }
    });

    return (
        <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-sky-100 to-indigo-100 px-4">
            <div className="w-full max-w-md bg-white/90 backdrop-blur-sm p-8 rounded-2xl shadow-xl border border-slate-200">

                <h2 className="text-3xl font-extrabold text-center text-slate-800 mb-2">
                    Create Account
                </h2>
                <p className="text-center text-slate-500 mb-6">
                    Join CampusTrack to report & recover items easily
                </p>

                <form onSubmit={formik.handleSubmit} className="space-y-5">

                    <div>
                        <label className="text-sm font-medium text-slate-700">Full Name</label>
                        <input
                            name="name"
                            type="text"
                            placeholder="John Doe"
                            className="w-full mt-1 px-4 py-3 rounded-lg border border-slate-300 focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 transition-all outline-none"
                            onChange={formik.handleChange}
                            value={formik.values.name}
                        />
                    </div>

                    <div>
                        <label className="text-sm font-medium text-slate-700">Email</label>
                        <input
                            name="email"
                            type="email"
                            placeholder="yourname@example.com"
                            className="w-full mt-1 px-4 py-3 rounded-lg border border-slate-300 focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 transition-all outline-none"
                            onChange={formik.handleChange}
                            value={formik.values.email}
                        />
                    </div>

                    <div>
                        <label className="text-sm font-medium text-slate-700">Password</label>
                        <input
                            name="password"
                            type="password"
                            placeholder="••••••••"
                            className="w-full mt-1 px-4 py-3 rounded-lg border border-slate-300 focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 transition-all outline-none"
                            onChange={formik.handleChange}
                            value={formik.values.password}
                        />
                    </div>

                    <div>
                        <label className="text-sm font-medium text-slate-700">Select Role</label>
                        <select
                            name="role"
                            className="w-full mt-1 px-4 py-3 rounded-lg border border-slate-300 focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 transition-all outline-none bg-white"
                            onChange={formik.handleChange}
                            value={formik.values.role}
                        >
                            <option value="STUDENT">Student</option>
                            <option value="ADMIN">Admin</option>
                        </select>
                    </div>

                    <button
                        type="submit"
                        disabled={formik.isSubmitting}
                        className="w-full py-3 bg-indigo-600 hover:bg-indigo-700 text-white rounded-lg font-semibold shadow-md transition-all disabled:opacity-60"
                    >
                        Register
                    </button>
                </form>

                <p className="text-center text-sm mt-5 text-slate-600">
                    Already have an account?{" "}
                    <span
                        className="text-indigo-600 font-semibold cursor-pointer hover:underline"
                        onClick={() => navigate("/login")}
                    >
                    Login
                </span>
                </p>
            </div>
        </div>
    );

}

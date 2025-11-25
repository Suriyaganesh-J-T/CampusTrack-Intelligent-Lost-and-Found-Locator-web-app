import React from "react";
import { useNavigate } from "react-router-dom";
import api from "../services/api";
import { useFormik } from "formik";
import * as yup from "yup";

const schema = yup.object({
    email: yup.string().email("Invalid email").required("Email required"),
    password: yup.string().required("Password required"),
});

export default function Login() {
    const navigate = useNavigate();

    const formik = useFormik({
        initialValues: { email: "", password: "" },
        validationSchema: schema,
        onSubmit: async (values, { setSubmitting }) => {
            try {
                const res = await api.post("/auth/login", values);
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
                    Welcome Back
                </h2>
                <p className="text-center text-slate-500 mb-6">
                    Login to continue
                </p>

                <form onSubmit={formik.handleSubmit} className="space-y-5">

                    <div>
                        <label className="text-sm font-medium text-slate-700">Email</label>
                        <input
                            name="email"
                            type="email"
                            placeholder="yourname@example.com"
                            className="w-full mt-1 px-4 py-3 rounded-lg border border-slate-300 focus:ring-2 focus:ring-sky-500 focus:border-sky-500 transition-all outline-none"
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
                            className="w-full mt-1 px-4 py-3 rounded-lg border border-slate-300 focus:ring-2 focus:ring-sky-500 focus:border-sky-500 transition-all outline-none"
                            onChange={formik.handleChange}
                            value={formik.values.password}
                        />
                    </div>

                    <button
                        type="submit"
                        disabled={formik.isSubmitting}
                        className="w-full py-3 bg-sky-600 hover:bg-sky-700 text-white rounded-lg font-semibold shadow-md transition-all disabled:opacity-60"
                    >
                        Login
                    </button>

                </form>

                <p className="text-center text-sm mt-5 text-slate-600">
                    Don’t have an account?{" "}
                    <span
                        className="text-sky-600 font-semibold cursor-pointer hover:underline"
                        onClick={() => navigate("/register")}
                    >
                    Register
                </span>
                </p>
            </div>
        </div>
    );
}

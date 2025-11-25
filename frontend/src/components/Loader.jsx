import React from "react";

export default function Loader({ text="Loading..." }) {
    return (
        <div className="flex items-center justify-center py-12">
            <div className="animate-spin rounded-full h-8 w-8 border-t-2 border-sky-500 mr-3"></div>
            <div className="text-slate-600">{text}</div>
        </div>
    );
}

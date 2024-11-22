import React from "react";
import { Routes, Route } from "react-router-dom";
import Login from "./page/auth/Login";

const App = () => {
  return (
      <Routes>
        <Route path="/" element={<Login />} />
      </Routes>
  );
};

export default App;

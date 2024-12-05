import { Link, Stack, Typography, Paper, Container } from "@mui/material";
import React from "react";
import { Link as RouterLink } from "react-router";
import LoginForm from "../../section/auth/LoginForm";

const Login = () => {
  return (
      <Container maxWidth="sm" sx={{ mt: 8 }}>
        <Paper elevation={3} sx={{ p: 4 }}>
          <Stack spacing={2} sx={{ mb: 5, textAlign: "center" }}>
            <Typography variant="h4">Login to Chat</Typography>
            <Typography variant="body1" color="text.secondary">
              Please login to your account
            </Typography>
            <Stack direction="row" spacing={0.5} justifyContent="center">
              <Typography variant="body2">New User?</Typography>
              <Link to="/auth/register" component={RouterLink} variant="subtitle2">
                Create an account
              </Link>
            </Stack>
          </Stack>

          <LoginForm />
        </Paper>
      </Container>
  );
};

export default Login;

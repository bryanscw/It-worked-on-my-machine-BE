# ============================================================
# Build stage 1: Builder
# ============================================================
FROM maven:3.6-jdk-11-slim as builder
ARG REVISION=dev
COPY  . /build
WORKDIR /build


# ============================================================
# Build stage 2: Production
# ============================================================
FROM openjdk:11-jre-alpine as production
ARG REVISION=dev
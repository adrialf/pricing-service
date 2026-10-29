# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- REST endpoint `GET /api/products` to list the product catalog with an optional free-text filter `q` (case-insensitive partial match).

## [1.0.0] - 2026-07-23

### Added
- REST endpoint `GET /api/prices/active` to query the applicable price for a product based on product ID, brand ID, and date.

# Kubernetes Deployment for K3s

This repository contains Kubernetes manifests for deploying our complete application stack on a K3s cluster using Kustomize. The resources are organized by components, and each component has its own Kustomize overlay for easy management and updates.

## Repository Structure

```bash
├── README.md 
├── kustomization.yml 
├── namespace.yml 
├── secrets.yml 
├── elasticsearch 
│   ├── deployment.yml 
│   ├── kustomization.yml 
│   └── service.yml 
├── frontend 
│   ├── deployment.yml 
│   ├── ingress.yml 
│   ├── kustomization.yml 
│   └── service.yml 
├── home-timeline-service 
│   ├── deployment.yml 
│   ├── ingress.yml 
│   ├── kustomization.yml 
│   └── service.yml 
├── mongo 
│   ├── init.js
│   ├── kustomization.yml
│   ├── service.yml 
│   └── statefulset.yml 
├── neo4j 
│   ├── deployment.yml 
│   ├── kustomization.yml 
│   └── service.yml 
├── redis 
│   ├── deployment.yml 
│   ├── kustomization.yml 
│   └── service.yml 
├── repo-post 
│   ├── deployment.yml 
│   ├── ingress.yml 
│   ├── kustomization.yml 
│   └── service.yml 
├── search-service 
│   ├── deployment.yml 
│   ├── ingress.yml 
│   ├── kustomization.yml 
│   └── service.yml 
├── social-service 
│   ├── deployment.yml 
│   ├── ingress.yml 
│   ├── kustomization.yml 
│   └── service.yml 
├── user-service 
│   ├── deployment.yml 
│   ├── ingress.yml 
│   ├── kustomization.yml 
│   └── service.yml 
└── user-timeline-service 
    ├── deployment.yml 
    ├── ingress.yml 
    ├── kustomization.yml 
    └── service.yml
```

### Overview

- **namespace.yml**: Defines the Kubernetes namespace where all resources will be deployed.
- **secrets.yml**: Contains secrets needed by various services.
- **Each component folder** (e.g., `elasticsearch`, `frontend`, `mongo`, etc.):  
  Contains the manifest files (deployments, services, ingresses, etc.) for that service along with an individual `kustomization.yml` file. This allows independent customization and updates.

- **Root kustomization.yml**: Aggregates all the subdirectories and additional files (such as namespace and secrets) to deploy the whole stack.

## Prerequisites

- **K3s Cluster**: Ensure you have a running K3s cluster.
- **kubectl**: Installed and configured to connect to your cluster.
- **Kustomize**: Integrated with `kubectl` (v1.14+); no separate installation is required.

## Deploying the Application


   ```bash
   kubectl apply -k .
   ```

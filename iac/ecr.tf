resource "aws_ecrpublic_repository" "tech-challenge-ecr" {

  repository_name     = "tech-challenge-fiap-followup"

  catalog_data {
    about_text        = "FIAP 2023 - Pós Tech - Tech Callenge - Fase 4"
  }

  tags = var.tag
}
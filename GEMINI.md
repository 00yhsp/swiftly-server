# Swiftly Server Context (AI Only)

이 파일은 AI 에이전트가 이 프로젝트를 효율적으로 이해하고 작업을 수행하기 위한 지침서입니다.

## 🏗️ Project Architecture
- **Language**: Kotlin
- **Framework**: Spring Boot 3.x
- **Build Tool**: Gradle (Kotlin DSL)
- **Architecture**: (정의되지 않음 - 추후 업데이트)

## 🎨 Coding Conventions
- **Naming**: Kotlin standard conventions (CamelCase)
- **Git**: [Gitmojis](https://gitmoji.dev/) 사용 권장
  - `feat`: ✨ 새 기능
  - `fix`: 🐛 버그 수정
  - `chore`: 🔧 설정 변경
  - `docs`: 📝 문서 수정

## 🚀 Workflows
- **Branching**: `main` <- `develop` <- `feature/{task-name}`
- **Validation**: 작업 완료 후 반드시 `./gradlew check` 실행

## ⚠️ Notes
- `GEMINI.md`는 `.gitignore`에 추가되어야 하며, AI에게 프로젝트 특화 정보를 전달할 때만 사용합니다.

IMAGE_NAME:=asaoweb/richtext-fs-editor
KUBE_STAGE_BASE_IMAGE:=$(IMAGE_NAME)-base
KUBE_STAGE_BUILD_IMAGE:=$(IMAGE_NAME)-build
IMAGE_TAG:=latest

default:
	cat ./Makefile
local:
	./mvnw clean package
dist:
	docker pull $(KUBE_STAGE_BASE_IMAGE):latest || true
	docker pull $(KUBE_STAGE_BUILD_IMAGE):latest || true
	docker pull $(IMAGE_NAME):latest || true
	docker build --target base --cache-from $(KUBE_STAGE_BASE_IMAGE):latest -t $(KUBE_STAGE_BASE_IMAGE):latest .
	docker build --target build --cache-from $(KUBE_STAGE_BASE_IMAGE):latest --cache-from $(KUBE_STAGE_BUILD_IMAGE):latest -t $(KUBE_STAGE_BUILD_IMAGE):latest .
	docker build --cache-from $(IMAGE_NAME) --cache-from $(KUBE_STAGE_BUILD_IMAGE):latest -t $(IMAGE_NAME):$(IMAGE_TAG) .
run:
	docker run -p 8080:8080 $(IMAGE_NAME):$(IMAGE_TAG)
run-bash:
	docker run -i -t $(IMAGE_NAME):$(IMAGE_TAG) /bin/bash
all: dist
push:
	docker push $(IMAGE_NAME):$(IMAGE_TAG)
	docker push $(IMAGE_NAME):latest
tag:
	git tag -m "richtext-fs-editor-v$(IMAGE_TAG)" -a "v$(IMAGE_TAG)"
	git push --tags
release-it: dist push

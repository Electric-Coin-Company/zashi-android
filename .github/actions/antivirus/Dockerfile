FROM clamav/clamav:1.0.1
RUN freshclam
ENTRYPOINT ["clamscan", "--recursive"]

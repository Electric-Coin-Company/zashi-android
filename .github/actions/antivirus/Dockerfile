FROM clamav/clamav:1.1.1
RUN freshclam
ENTRYPOINT ["clamscan", "--recursive"]

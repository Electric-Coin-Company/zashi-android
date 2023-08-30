FROM clamav/clamav:1.2.0
RUN freshclam
ENTRYPOINT ["clamscan", "--recursive"]
